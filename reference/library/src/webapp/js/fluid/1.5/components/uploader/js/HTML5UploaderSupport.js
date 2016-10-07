var fluid_1_5=fluid_1_5||{};!function($,fluid){"use strict";fluid.demands("fluid.uploaderImpl","fluid.uploader.html5",{horizon:"fluid.uploader.progressiveCheck",funcName:"fluid.uploader.multiFileUploader"}),fluid.demands("fluid.uploader.strategy","fluid.uploader.html5",{horizon:"fluid.uploader.progressiveCheck",funcName:"fluid.uploader.html5Strategy"}),fluid.defaults("fluid.uploader.html5Strategy",{gradeNames:["fluid.uploader.strategy","autoInit"],components:{local:{type:"fluid.uploader.html5Strategy.local"}}}),fluid.uploader.html5Strategy.fileSuccessHandler=function(file,events,xhr){events.onFileSuccess.fire(file,xhr.responseText,xhr),events.onFileComplete.fire(file)},fluid.uploader.html5Strategy.fileErrorHandler=function(file,events,xhr){events.onFileError.fire(file,fluid.uploader.errorConstants.UPLOAD_FAILED,xhr.status,xhr),events.onFileComplete.fire(file)},fluid.uploader.html5Strategy.fileStopHandler=function(file,events,xhr){events.onFileError.fire(file,fluid.uploader.errorConstants.UPLOAD_STOPPED,xhr.status,xhr),events.onFileComplete.fire(file)},fluid.uploader.html5Strategy.monitorFileUploadXHR=function(file,events,xhr){xhr.onreadystatechange=function(){if(4===xhr.readyState){var status=xhr.status;status>=200&&204>=status?fluid.uploader.html5Strategy.fileSuccessHandler(file,events,xhr):0===status?fluid.uploader.html5Strategy.fileStopHandler(file,events,xhr):fluid.uploader.html5Strategy.fileErrorHandler(file,events,xhr)}},xhr.upload.onprogress=function(pe){events.onFileProgress.fire(file,pe.loaded,pe.total)}},fluid.uploader.html5Strategy.uploadNextFile=function(queue,uploadFile){var batch=queue.currentBatch,file=batch.files[batch.fileIdx];uploadFile(file)},fluid.uploader.html5Strategy.uploadFile=function(that,file){that.events.onFileStart.fire(file),that.currentXHR=that.createXHR(),fluid.uploader.html5Strategy.monitorFileUploadXHR(file,that.events,that.currentXHR),that.fileSender.send(file,that.queueSettings,that.currentXHR)},fluid.uploader.html5Strategy.stop=function(that){that.queue.isUploading=!1,that.currentXHR.abort(),that.events.onUploadStop.fire()},fluid.defaults("fluid.uploader.html5Strategy.remote",{gradeNames:["fluid.uploader.remote","autoInit"],components:{fileSender:{type:"fluid.uploader.html5Strategy.fileSender"}},invokers:{createXHR:"fluid.uploader.html5Strategy.createXHR",uploadNextFile:{funcName:"fluid.uploader.html5Strategy.uploadNextFile",args:["{that}.queue","{that}.uploadFile"]},uploadFile:{funcName:"fluid.uploader.html5Strategy.uploadFile",args:["{that}","{arguments}.0"]},stop:{funcName:"fluid.uploader.html5Strategy.stop",args:["{that}"]}}}),fluid.demands("fluid.uploader.remote",["fluid.uploader.html5Strategy","fluid.uploader.live"],{funcName:"fluid.uploader.html5Strategy.remote"}),fluid.uploader.html5Strategy.createXHR=function(){return new XMLHttpRequest},fluid.uploader.html5Strategy.createFormData=function(){return new FormData},fluid.uploader.html5Strategy.setPostParams=function(formData,postParams){$.each(postParams,function(key,value){formData.append(key,value)})},fluid.uploader.html5Strategy.fileSender=function(){fluid.fail("Error instantiating HTML5 Uploader - browser does not support FormData feature. Please try version 1.4 or earlier of Uploader which has Firefox 3.x support")},fluid.defaults("fluid.uploader.html5Strategy.formDataSender",{gradeNames:["fluid.littleComponent","autoInit"],invokers:{createFormData:"fluid.uploader.html5Strategy.createFormData",send:{funcName:"fluid.uploader.html5Strategy.sendFormData",args:["{that}.createFormData","{arguments}.0","{arguments}.1","{arguments}.2"]}}}),fluid.uploader.html5Strategy.sendFormData=function(formCreator,file,queueSettings,xhr){var formData=formCreator();return formData.append("file",file),fluid.uploader.html5Strategy.setPostParams(formData,queueSettings.postParams),xhr.open("POST",queueSettings.uploadURL,!0),xhr.send(formData),formData},fluid.demands("fluid.uploader.html5Strategy.fileSender",["fluid.uploader.html5Strategy.remote","fluid.browser.supportsFormData"],{funcName:"fluid.uploader.html5Strategy.formDataSender"}),fluid.defaults("fluid.uploader.html5Strategy.local",{gradeNames:["fluid.uploader.local","autoInit"],invokers:{addFiles:{funcName:"fluid.uploader.html5Strategy.local.addFiles",args:["{that}","{arguments}.0"]},removeFile:"fluid.identity",enableBrowseButton:"{that}.browseButtonView.enable",disableBrowseButton:"{that}.browseButtonView.disable"},components:{browseButtonView:{type:"fluid.uploader.html5Strategy.browseButtonView",options:{strings:"{uploader}.options.strings.buttons",queueSettings:"{uploader}.options.queueSettings",selectors:{browseButton:"{uploader}.options.selectors.browseButton"},listeners:{onFilesQueued:"{local}.addFiles"}}}}}),fluid.uploader.html5Strategy.local.addFiles=function(that,files){var queueSettings=that.options.queueSettings,sizeLimit=1024*queueSettings.fileSizeLimit,fileLimit=queueSettings.fileUploadLimit,uploaded=that.queue.getUploadedFiles().length,queued=that.queue.getReadyFiles().length,remainingUploadLimit=fileLimit-uploaded-queued;that.events.onFilesSelected.fire(files.length);for(var numFilesAdded=0,i=0;i<files.length;i++){var file=files[i];fileLimit&&0===remainingUploadLimit?that.events.onQueueError.fire(file,fluid.uploader.queueErrorConstants.QUEUE_LIMIT_EXCEEDED):file.size>=sizeLimit?(file.filestatus=fluid.uploader.fileStatusConstants.ERROR,that.events.onQueueError.fire(file,fluid.uploader.queueErrorConstants.FILE_EXCEEDS_SIZE_LIMIT)):(!fileLimit||remainingUploadLimit>0)&&(file.id="file-"+fluid.allocateGuid(),file.filestatus=fluid.uploader.fileStatusConstants.QUEUED,that.events.afterFileQueued.fire(file),remainingUploadLimit--,numFilesAdded++)}that.events.afterFileDialog.fire(numFilesAdded)},fluid.uploader.bindEventsToFileInput=function(that,fileInput){fileInput.click(function(){that.events.onBrowse.fire()}),fileInput.change(function(){var files=fileInput[0].files;that.renderFreshMultiFileInput(),that.events.onFilesQueued.fire(files)}),fileInput.focus(function(){that.browseButton.addClass("focus"),that.events.onFocusFileInput.fire(that,fileInput,!0)}),fileInput.blur(function(){that.browseButton.removeClass("focus"),that.events.onFocusFileInput.fire(that,fileInput,!1)})},fluid.uploader.renderMultiFileInput=function(that){var multiFileInput=$(that.options.multiFileInputMarkup),fileTypes=that.options.queueSettings.fileTypes;return fluid.isArrayable(fileTypes)&&(fileTypes=fileTypes.join(),multiFileInput.attr("accept",fileTypes)),multiFileInput},fluid.uploader.renderFreshMultiFileInput=function(that){var previousInput=that.locate("fileInputs").last();previousInput.hide(),previousInput.prop("tabindex",-1);var newInput=fluid.uploader.renderMultiFileInput(that);newInput.attr("aria-label",that.options.strings.addMore),previousInput.after(newInput),fluid.uploader.bindEventsToFileInput(that,newInput)},fluid.uploader.setupBrowseButtonView=function(that){var multiFileInput=fluid.uploader.renderMultiFileInput(that);multiFileInput.attr("aria-label",that.options.strings.browse),that.browseButton.append(multiFileInput),fluid.uploader.bindEventsToFileInput(that,multiFileInput),that.browseButton.prop("tabindex",-1)},fluid.uploader.isEnabled=function(element){return!element.prop("disabled")},fluid.defaults("fluid.uploader.html5Strategy.browseButtonView",{gradeNames:["fluid.viewComponent","autoInit"],strings:{browse:"Browse files",addMore:"Add more"},multiFileInputMarkup:"<input type='file' multiple='' class='flc-uploader-html5-input' />",queueSettings:{},members:{browseButton:"{that}.dom.browseButton"},invokers:{enable:{"this":"{that}.dom.fileInputs",method:"prop",args:["disabled",!1]},disable:{"this":"{that}.dom.fileInputs",method:"prop",args:["disabled",!0]},isEnabled:{funcName:"fluid.uploader.isEnabled",args:"{that}.dom.fileInputs"},renderFreshMultiFileInput:{funcName:"fluid.uploader.renderFreshMultiFileInput",args:"{that}"}},selectors:{browseButton:".flc-uploader-button-browse",fileInputs:".flc-uploader-html5-input"},events:{onFocusFileInput:null,onBrowse:null,onFilesQueued:null},listeners:{onCreate:{funcName:"fluid.uploader.setupBrowseButtonView",args:"{that}"}}}),fluid.demands("fluid.uploader.html5Strategy.browseButtonView","fluid.uploader.html5Strategy.local",{container:"{multiFileUploader}.container",mergeOptions:{events:{onBrowse:"{local}.events.onFileDialog"}}})}(jQuery,fluid_1_5);
