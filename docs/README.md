# CheckMate Architecture Documentation

CheckMate adopts a multi-module architecture based on Layered Architecture and MVVM/MVI patterns.

## Module Dependency Graph

```mermaid
graph LR
    %% スタイル定義
    classDef app fill:#BBDEFB,stroke:#1976D2,color:black
    classDef feature fill:#E1BEE7,stroke:#7B1FA2,color:black
    classDef ui fill:#F8BBD0,stroke:#C2185B,color:black
    classDef domain fill:#C8E6C9,stroke:#388E3C,color:black
    classDef data fill:#FFE0B2,stroke:#F57C00,color:black

    App(":app"):::app

    subgraph Presentation [Presentation Layer]
        direction TB
        Nav(":presentation:navigation"):::ui
        
        subgraph Features [Feature Modules]
            Splash(":feature:splash"):::feature
            Login(":feature:login"):::feature
            Home(":feature:home"):::feature
            Dashboard(":feature:dashboard"):::feature
            Items(":feature:items"):::feature
            Templates(":feature:templates"):::feature
            Setting(":feature:setting"):::feature
        end
        
        UI(":presentation:ui"):::ui
        CommonPres(":presentation:common"):::ui
        Design(":presentation:designsystem"):::ui
    end

    subgraph Domain [Domain Layer]
        direction TB
        UseCase(":domain:usecase"):::domain
        DomainModel(":domain:model"):::domain
        DomainRepo(":domain:repository"):::domain
    end

    subgraph Data [Data Layer]
        direction TB
        DataRepo(":data:repository"):::data
        Local(":data:local"):::data
        Remote(":data:remote"):::data
    end

    %% --- Main Dependency Flow (実線: 主要な流れ) ---
    App --> Nav
    Nav --> Splash & Login & Home & Dashboard & Items & Templates & Setting
    
    Splash & Login & Home & Dashboard & Items & Templates & Setting --> UseCase
    
    UseCase --> DomainRepo
    DataRepo --> DomainRepo
    DataRepo --> Local & Remote

    %% --- Auxiliary Dependencies (点線: 補助的・共通利用) ---
    %% App Setup
    App -.-> DataRepo & UseCase & DomainModel & UI & Splash & Login & Dashboard & Home & Items & Setting & Templates

    %% UI & Common
    Nav -.-> UI
    Splash & Login & Home & Dashboard & Items & Templates & Setting -.-> CommonPres
    CommonPres -.-> UI
    UI -.-> Design
    UI -.-> DomainModel

    %% Domain & Data Details
    UseCase -.-> DomainModel
    DomainRepo -.-> DomainModel
    DataRepo -.-> DomainModel
    Local & Remote -.-> DomainModel
```

## Module Descriptions

### App (`:app`)
The application entry point. It configures Dependency Injection (Koin), initializes the app, and launches the main activity.

### Presentation Layer
- **`:presentation:navigation`**: Handles the application's navigation graph and routing logic. It orchestrates transitions between different features.
- **`:presentation:feature:*`**: Contains the UI and logic for specific features (screens). Each feature module is independent and exposes an entry point for navigation.
- **`:presentation:ui`**: Contains reusable UI components, base classes (BaseViewModel, BaseFragment), and common UI utilities.
- **`:presentation:designsystem`**: Defines the design language (colors, typography, shapes, themes) and atomic UI components.
- **`:presentation:common`**: Shared logic and utilities specific to the presentation layer.

### Domain Layer
- **`:domain:usecase`**: Contains business logic encapsulated as Use Cases. It depends only on the Domain Model and Repository interfaces.
- **`:domain:model`**: Defines the core business objects and data structures used throughout the application. It has no dependencies on Android frameworks.
- **`:domain:repository`**: Defines the interfaces for data access (Repository pattern).

### Data Layer
- **`:data:repository`**: Implements the repository interfaces defined in the Domain layer. It coordinates data fetching from local and remote sources.
- **`:data:local`**: Handles local data storage (e.g., Room database, DataStore).
- **`:data:remote`**: Handles network communication (e.g., Retrofit, API calls).


