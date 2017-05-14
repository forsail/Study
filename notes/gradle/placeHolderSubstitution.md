Libraryplugin -> BasePlugin.createTasks -> createAndroidTasks -> variantManager.createAndroidTasks()

对于没有任何变种的情况下:

-> populateVariantDataList()->createVariantDataForProductFlavors()



variantManager.createTasksForVariantData()->taskManager.createTasksForVariantData->LibraryTaskManager.createMergeLibManifestsTask 创建 ProcessManifest 任务.



