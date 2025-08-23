package com.segnities007.repository

import android.net.Uri
import com.segnities007.model.WeeklyTemplate
import com.segnities007.model.calendar.CalendarEvent

interface IcsTemplateRepository {
    /**
     * ICSファイルからカレンダーイベントを読み込み、テンプレートを生成
     * @param uri ICSファイルのURI
     * @return 生成されたテンプレートのリスト
     */
    suspend fun generateTemplatesFromIcs(uri: Uri): List<WeeklyTemplate>
    
    /**
     * カレンダーイベントからテンプレートを生成
     * @param events カレンダーイベントのリスト
     * @return 生成されたテンプレートのリスト
     */
    suspend fun generateTemplatesFromEvents(events: List<CalendarEvent>): List<WeeklyTemplate>
    
    /**
     * 生成されたテンプレートを保存
     * @param templates 保存するテンプレートのリスト
     */
    suspend fun saveGeneratedTemplates(templates: List<WeeklyTemplate>)
}
