package com.segnities007.common.module

import com.segnities007.usecase.backup.ExportDataUseCase
import com.segnities007.usecase.backup.ImportDataUseCase
import com.segnities007.usecase.checkstate.CheckItemUseCase
import com.segnities007.usecase.checkstate.ClearAllCheckStatesUseCase
import com.segnities007.usecase.checkstate.EnsureCheckHistoryForTodayUseCase
import com.segnities007.usecase.checkstate.GetCheckStateForItemUseCase
import com.segnities007.usecase.checkstate.GetCheckStatesForItemsUseCase
import com.segnities007.usecase.checkstate.SaveCheckStateUseCase
import com.segnities007.usecase.ics.GenerateTemplatesFromIcsUseCase
import com.segnities007.usecase.ics.SaveGeneratedTemplatesUseCase
import com.segnities007.usecase.image.DeleteImageUseCase
import com.segnities007.usecase.image.SaveImageUseCase
import com.segnities007.usecase.item.AddItemUseCase
import com.segnities007.usecase.item.ClearAllItemsUseCase
import com.segnities007.usecase.item.DeleteItemUseCase
import com.segnities007.usecase.item.GetAllItemsUseCase
import com.segnities007.usecase.item.GetItemByIdUseCase
import com.segnities007.usecase.item.GetProductInfoByBarcodeUseCase
import com.segnities007.usecase.item.UpdateItemUseCase
import com.segnities007.usecase.template.AddTemplateUseCase
import com.segnities007.usecase.template.ClearAllTemplatesUseCase
import com.segnities007.usecase.template.DeleteTemplateUseCase
import com.segnities007.usecase.template.GetAllTemplatesUseCase
import com.segnities007.usecase.template.GetTemplatesForDayUseCase
import com.segnities007.usecase.template.UpdateTemplateUseCase
import com.segnities007.usecase.user.CreateAccountUseCase
import com.segnities007.usecase.user.GetUserStatusUseCase
import com.segnities007.usecase.user.IsAccountCreatedUseCase
import com.segnities007.usecase.user.LoginWithGoogleUseCase
import org.koin.dsl.module

val useCaseModule = module {
    // Item Use Cases
    factory { GetAllItemsUseCase(get()) }
    factory { GetItemByIdUseCase(get()) }
    factory { AddItemUseCase(get()) }
    factory { UpdateItemUseCase(get()) }
    factory { DeleteItemUseCase(get()) }
    factory { ClearAllItemsUseCase(get()) }
    factory { GetProductInfoByBarcodeUseCase(get()) }
    
    // Template Use Cases
    factory { GetAllTemplatesUseCase(get()) }
    factory { GetTemplatesForDayUseCase(get()) }
    factory { AddTemplateUseCase(get()) }
    factory { UpdateTemplateUseCase(get()) }
    factory { DeleteTemplateUseCase(get()) }
    factory { ClearAllTemplatesUseCase(get()) }
    
    // CheckState Use Cases
    factory { GetCheckStateForItemUseCase(get()) }
    factory { GetCheckStatesForItemsUseCase(get()) }
    factory { SaveCheckStateUseCase(get()) }
    factory { ClearAllCheckStatesUseCase(get()) }
    factory { CheckItemUseCase(get()) }
    factory { EnsureCheckHistoryForTodayUseCase(get(), get(), get()) }
    
    // User Use Cases
    factory { IsAccountCreatedUseCase(get()) }
    factory { GetUserStatusUseCase(get()) }
    factory { CreateAccountUseCase(get()) }
    factory { LoginWithGoogleUseCase(get()) }
    
    // Backup Use Cases
    factory { ExportDataUseCase(get()) }
    factory { ImportDataUseCase(get()) }
    
    // ICS Use Cases
    factory { GenerateTemplatesFromIcsUseCase(get()) }
    factory { SaveGeneratedTemplatesUseCase(get()) }
    
    // Image Use Cases
    factory { SaveImageUseCase(get()) }
    factory { DeleteImageUseCase(get()) }
}
