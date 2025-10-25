"""
...existing code...
"""
# ...existing code...
#!/usr/bin/env python3
"""
Remove comments from source files in the repository while preserving string literals and shebangs.
Backs up each changed file under .comment_backups/<relative_path>.bak
Usage: python3 scripts/remove_comments.py [--dry-run] [--extensions .kt,.kts] [--exclude build,.git]
"""
import os
import sys
import io
import argparse
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
BACKUP_DIR = ROOT / '.comment_backups'

# File extensions grouped by comment style
C_LIKE = {'.kt', '.kts', '.java', '.groovy', '.gradle', '.js', '.jsx', '.ts', '.tsx'}
XML_LIKE = {'.xml', '.html', '.htm', '.md'}
HASH_LIKE = {'.sh', '.bash', '.zsh', '.env', '.properties', '.yml', '.yaml'}

# Some filenames to treat as shell (Dockerfile has no extension sometimes)
SPECIAL_FILENAMES = {'gradlew', 'gradlew.bat', 'Dockerfile'}

# --- comment removal helpers (improved) ---

def remove_c_like_comments(s: str) -> str:
    out = []
    i = 0
    n = len(s)
    in_block = False
    in_line = False
    in_double = False
    in_single = False
    in_triple = False  # for Kotlin triple-quoted """
    in_backtick = False  # for JS/TS template literals
    while i < n:
        if in_block:
            if s.startswith('*/', i):
                in_block = False
                i += 2
            else:
                i += 1
        elif in_line:
            if s[i] == '\n':
                in_line = False
                out.append('\n')
                i += 1
            else:
                i += 1
        elif in_triple:
            if s.startswith('"""', i):
                out.append('"""')
                in_triple = False
                i += 3
            else:
                out.append(s[i])
                i += 1
        elif in_backtick:
            # preserve until next unescaped backtick
            if s[i] == '\\':
                if i + 1 < n:
                    out.append(s[i:i+2])
                    i += 2
                else:
                    out.append(s[i])
                    i += 1
            elif s[i] == '`':
                out.append('`')
                in_backtick = False
                i += 1
            else:
                out.append(s[i])
                i += 1
        elif in_double:
            if s[i] == '\\':
                # preserve escaped char
                if i + 1 < n:
                    out.append(s[i:i+2])
                    i += 2
                else:
                    out.append(s[i])
                    i += 1
            elif s[i] == '"':
                out.append('"')
                in_double = False
                i += 1
            else:
                out.append(s[i])
                i += 1
        elif in_single:
            if s[i] == '\\':
                if i + 1 < n:
                    out.append(s[i:i+2])
                    i += 2
                else:
                    out.append(s[i])
                    i += 1
            elif s[i] == "'":
                out.append("'")
                in_single = False
                i += 1
            else:
                out.append(s[i])
                i += 1
        else:
            # not in any string/comment
            if s.startswith('/*', i):
                in_block = True
                i += 2
            elif s.startswith('//', i):
                in_line = True
                i += 2
            elif s.startswith('"""', i):
                in_triple = True
                out.append('"""')
                i += 3
            elif s[i] == '`':
                in_backtick = True
                out.append('`')
                i += 1
            elif s[i] == '"':
                in_double = True
                out.append('"')
                i += 1
            elif s[i] == "'":
                in_single = True
                out.append("'")
                i += 1
            else:
                out.append(s[i])
                i += 1
    return ''.join(out)


def remove_xml_comments(s: str) -> str:
    out = []
    i = 0
    n = len(s)
    in_quote = None
    while i < n:
        if in_quote:
            if s[i] == '\\':
                # preserve escaped
                if i + 1 < n:
                    out.append(s[i:i+2])
                    i += 2
                else:
                    out.append(s[i])
                    i += 1
            elif s[i] == in_quote:
                out.append(s[i])
                in_quote = None
                i += 1
            else:
                out.append(s[i])
                i += 1
        else:
            if s.startswith('<!--', i):
                # skip until -->
                j = s.find('-->', i+4)
                if j == -1:
                    # rest of file is comment -> stop
                    break
                else:
                    i = j + 3
            elif s[i] == '"' or s[i] == "'":
                in_quote = s[i]
                out.append(s[i])
                i += 1
            else:
                out.append(s[i])
                i += 1
    return ''.join(out)


def remove_hash_comments(s: str, keep_shebang=True) -> str:
    lines = s.splitlines(keepends=True)
    out_lines = []
    for idx, line in enumerate(lines):
        stripped = line.lstrip()
        if keep_shebang and idx == 0 and stripped.startswith('#!'):
            out_lines.append(line)
            continue
        # If line's first non-space is # or ! -> drop entire line
        if stripped.startswith('#') or stripped.startswith('!'):
            if line.endswith('\n'):
                out_lines.append('\n')
            else:
                pass
            continue
        # otherwise, remove inline # comments but only when # is preceded by whitespace
        new_line = []
        in_double = False
        in_single = False
        i = 0
        n = len(line)
        removed = False
        while i < n:
            ch = line[i]
            if ch == '\\':
                if i + 1 < n:
                    new_line.append(line[i:i+2])
                    i += 2
                else:
                    new_line.append(ch)
                    i += 1
                continue
            if in_double:
                if ch == '"':
                    in_double = False
                new_line.append(ch)
                i += 1
                continue
            if in_single:
                if ch == "'":
                    in_single = False
                new_line.append(ch)
                i += 1
                continue
            if ch == '"':
                in_double = True
                new_line.append(ch)
                i += 1
                continue
            if ch == "'":
                in_single = True
                new_line.append(ch)
                i += 1
                continue
            if ch == '#':
                prev = line[i-1] if i-1 >= 0 else None
                if prev is None or prev.isspace():
                    removed = True
                    break
                else:
                    new_line.append(ch)
                    i += 1
                    continue
            else:
                new_line.append(ch)
                i += 1
        if removed:
            if line.endswith('\n'):
                new_line.append('\n')
            out_lines.append(''.join(new_line))
        else:
            out_lines.append(''.join(new_line))
    return ''.join(out_lines)


def process_file(path: Path, dry_run: bool = True) -> bool:
    rel = path.relative_to(ROOT)
    ext = path.suffix
    name = path.name
    try:
        orig = path.read_text(encoding='utf-8')
    except Exception:
        return False
    new = orig
    lower_name = name.lower()
    if ext in C_LIKE or ext == '.gradle' or lower_name in SPECIAL_FILENAMES or ext in {'.kts'}:
        new = remove_c_like_comments(orig)
    elif ext in XML_LIKE:
        new = remove_xml_comments(orig)
    elif ext in HASH_LIKE or name in SPECIAL_FILENAMES:
        new = remove_hash_comments(orig, keep_shebang=True)
    else:
        return False
    if new != orig:
        if dry_run:
            print(f"WILL MODIFY: {rel}")
            return True
        backup_path = BACKUP_DIR / rel
        backup_path.parent.mkdir(parents=True, exist_ok=True)
        backup_path.write_text(orig, encoding='utf-8')
        path.write_text(new, encoding='utf-8')
        print(f"MODIFIED: {rel}")
        return True
    return False


def is_text_file(path: Path) -> bool:
    try:
        path.read_text(encoding='utf-8')
        return True
    except Exception:
        return False


def should_exclude(path: Path, exclude_dirs):
    for part in path.parts:
        if part in exclude_dirs:
            return True
    return False


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument('--root', default=str(ROOT), help='Repository root')
    parser.add_argument('--dry-run', action='store_true', help='Do not write files; only list candidates')
    parser.add_argument('--extensions', default=None, help='Comma-separated extensions to include (e.g. .kt,.kts,.xml)')
    parser.add_argument('--exclude', default=None, help='Comma-separated directory names to exclude (e.g. build,.git)')
    args = parser.parse_args()
    root = Path(args.root)
    dry_run = args.dry_run
    exts = None
    if args.extensions:
        exts = {e.strip() if e.strip().startswith('.') else '.' + e.strip() for e in args.extensions.split(',')}
    exclude_dirs = set()
    if args.exclude:
        exclude_dirs = {e.strip() for e in args.exclude.split(',')}
    changed = []
    scanned = 0
    for dirpath, dirnames, filenames in os.walk(root):
        if '.comment_backups' in dirpath:
            continue
        for fname in filenames:
            path = Path(dirpath) / fname
            if should_exclude(path, exclude_dirs):
                continue
            # skip build directories
            if any(part in ('build', '.gradle', '.git', '.idea', 'out') for part in path.parts):
                continue
            ext = path.suffix
            if exts is not None and ext not in exts and fname not in SPECIAL_FILENAMES:
                continue
            if fname in SPECIAL_FILENAMES or ext in C_LIKE or ext in XML_LIKE or ext in HASH_LIKE or ext == '.kts':
                if not is_text_file(path):
                    continue
                scanned += 1
                try:
                    ok = process_file(path, dry_run=dry_run)
                    if ok:
                        changed.append(str(path.relative_to(root)))
                except Exception as e:
                    print(f"ERROR processing {path}: {e}")
    print('\nSummary:')
    print(f'Scanned files: {scanned}')
    print(f'Files to modify: {len(changed)}')
    for c in changed:
        print(' -', c)

if __name__ == '__main__':
    main()

