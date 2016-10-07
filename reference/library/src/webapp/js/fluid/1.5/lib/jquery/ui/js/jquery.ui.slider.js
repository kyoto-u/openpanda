!function($,undefined){var numPages=5;$.widget("ui.slider",$.ui.mouse,{version:"1.10.4",widgetEventPrefix:"slide",options:{animate:!1,distance:0,max:100,min:0,orientation:"horizontal",range:!1,step:1,value:0,values:null,change:null,slide:null,start:null,stop:null},_create:function(){this._keySliding=!1,this._mouseSliding=!1,this._animateOff=!0,this._handleIndex=null,this._detectOrientation(),this._mouseInit(),this.element.addClass("ui-slider ui-slider-"+this.orientation+" ui-widget ui-widget-content ui-corner-all"),this._refresh(),this._setOption("disabled",this.options.disabled),this._animateOff=!1},_refresh:function(){this._createRange(),this._createHandles(),this._setupEvents(),this._refreshValue()},_createHandles:function(){var i,handleCount,options=this.options,existingHandles=this.element.find(".ui-slider-handle").addClass("ui-state-default ui-corner-all"),handle="<a class='ui-slider-handle ui-state-default ui-corner-all' href='#'></a>",handles=[];for(handleCount=options.values&&options.values.length||1,existingHandles.length>handleCount&&(existingHandles.slice(handleCount).remove(),existingHandles=existingHandles.slice(0,handleCount)),i=existingHandles.length;handleCount>i;i++)handles.push(handle);this.handles=existingHandles.add($(handles.join("")).appendTo(this.element)),this.handle=this.handles.eq(0),this.handles.each(function(i){$(this).data("ui-slider-handle-index",i)})},_createRange:function(){var options=this.options,classes="";options.range?(options.range===!0&&(options.values?options.values.length&&2!==options.values.length?options.values=[options.values[0],options.values[0]]:$.isArray(options.values)&&(options.values=options.values.slice(0)):options.values=[this._valueMin(),this._valueMin()]),this.range&&this.range.length?this.range.removeClass("ui-slider-range-min ui-slider-range-max").css({left:"",bottom:""}):(this.range=$("<div></div>").appendTo(this.element),classes="ui-slider-range ui-widget-header ui-corner-all"),this.range.addClass(classes+("min"===options.range||"max"===options.range?" ui-slider-range-"+options.range:""))):(this.range&&this.range.remove(),this.range=null)},_setupEvents:function(){var elements=this.handles.add(this.range).filter("a");this._off(elements),this._on(elements,this._handleEvents),this._hoverable(elements),this._focusable(elements)},_destroy:function(){this.handles.remove(),this.range&&this.range.remove(),this.element.removeClass("ui-slider ui-slider-horizontal ui-slider-vertical ui-widget ui-widget-content ui-corner-all"),this._mouseDestroy()},_mouseCapture:function(event){var position,normValue,distance,closestHandle,index,allowed,offset,mouseOverHandle,that=this,o=this.options;return o.disabled?!1:(this.elementSize={width:this.element.outerWidth(),height:this.element.outerHeight()},this.elementOffset=this.element.offset(),position={x:event.pageX,y:event.pageY},normValue=this._normValueFromMouse(position),distance=this._valueMax()-this._valueMin()+1,this.handles.each(function(i){var thisDistance=Math.abs(normValue-that.values(i));(distance>thisDistance||distance===thisDistance&&(i===that._lastChangedValue||that.values(i)===o.min))&&(distance=thisDistance,closestHandle=$(this),index=i)}),allowed=this._start(event,index),allowed===!1?!1:(this._mouseSliding=!0,this._handleIndex=index,closestHandle.addClass("ui-state-active").focus(),offset=closestHandle.offset(),mouseOverHandle=!$(event.target).parents().addBack().is(".ui-slider-handle"),this._clickOffset=mouseOverHandle?{left:0,top:0}:{left:event.pageX-offset.left-closestHandle.width()/2,top:event.pageY-offset.top-closestHandle.height()/2-(parseInt(closestHandle.css("borderTopWidth"),10)||0)-(parseInt(closestHandle.css("borderBottomWidth"),10)||0)+(parseInt(closestHandle.css("marginTop"),10)||0)},this.handles.hasClass("ui-state-hover")||this._slide(event,index,normValue),this._animateOff=!0,!0))},_mouseStart:function(){return!0},_mouseDrag:function(event){var position={x:event.pageX,y:event.pageY},normValue=this._normValueFromMouse(position);return this._slide(event,this._handleIndex,normValue),!1},_mouseStop:function(event){return this.handles.removeClass("ui-state-active"),this._mouseSliding=!1,this._stop(event,this._handleIndex),this._change(event,this._handleIndex),this._handleIndex=null,this._clickOffset=null,this._animateOff=!1,!1},_detectOrientation:function(){this.orientation="vertical"===this.options.orientation?"vertical":"horizontal"},_normValueFromMouse:function(position){var pixelTotal,pixelMouse,percentMouse,valueTotal,valueMouse;return"horizontal"===this.orientation?(pixelTotal=this.elementSize.width,pixelMouse=position.x-this.elementOffset.left-(this._clickOffset?this._clickOffset.left:0)):(pixelTotal=this.elementSize.height,pixelMouse=position.y-this.elementOffset.top-(this._clickOffset?this._clickOffset.top:0)),percentMouse=pixelMouse/pixelTotal,percentMouse>1&&(percentMouse=1),0>percentMouse&&(percentMouse=0),"vertical"===this.orientation&&(percentMouse=1-percentMouse),valueTotal=this._valueMax()-this._valueMin(),valueMouse=this._valueMin()+percentMouse*valueTotal,this._trimAlignValue(valueMouse)},_start:function(event,index){var uiHash={handle:this.handles[index],value:this.value()};return this.options.values&&this.options.values.length&&(uiHash.value=this.values(index),uiHash.values=this.values()),this._trigger("start",event,uiHash)},_slide:function(event,index,newVal){var otherVal,newValues,allowed;this.options.values&&this.options.values.length?(otherVal=this.values(index?0:1),2===this.options.values.length&&this.options.range===!0&&(0===index&&newVal>otherVal||1===index&&otherVal>newVal)&&(newVal=otherVal),newVal!==this.values(index)&&(newValues=this.values(),newValues[index]=newVal,allowed=this._trigger("slide",event,{handle:this.handles[index],value:newVal,values:newValues}),otherVal=this.values(index?0:1),allowed!==!1&&this.values(index,newVal))):newVal!==this.value()&&(allowed=this._trigger("slide",event,{handle:this.handles[index],value:newVal}),allowed!==!1&&this.value(newVal))},_stop:function(event,index){var uiHash={handle:this.handles[index],value:this.value()};this.options.values&&this.options.values.length&&(uiHash.value=this.values(index),uiHash.values=this.values()),this._trigger("stop",event,uiHash)},_change:function(event,index){if(!this._keySliding&&!this._mouseSliding){var uiHash={handle:this.handles[index],value:this.value()};this.options.values&&this.options.values.length&&(uiHash.value=this.values(index),uiHash.values=this.values()),this._lastChangedValue=index,this._trigger("change",event,uiHash)}},value:function(newValue){return arguments.length?(this.options.value=this._trimAlignValue(newValue),this._refreshValue(),void this._change(null,0)):this._value()},values:function(index,newValue){var vals,newValues,i;if(arguments.length>1)return this.options.values[index]=this._trimAlignValue(newValue),this._refreshValue(),void this._change(null,index);if(!arguments.length)return this._values();if(!$.isArray(arguments[0]))return this.options.values&&this.options.values.length?this._values(index):this.value();for(vals=this.options.values,newValues=arguments[0],i=0;i<vals.length;i+=1)vals[i]=this._trimAlignValue(newValues[i]),this._change(null,i);this._refreshValue()},_setOption:function(key,value){var i,valsLength=0;switch("range"===key&&this.options.range===!0&&("min"===value?(this.options.value=this._values(0),this.options.values=null):"max"===value&&(this.options.value=this._values(this.options.values.length-1),this.options.values=null)),$.isArray(this.options.values)&&(valsLength=this.options.values.length),$.Widget.prototype._setOption.apply(this,arguments),key){case"orientation":this._detectOrientation(),this.element.removeClass("ui-slider-horizontal ui-slider-vertical").addClass("ui-slider-"+this.orientation),this._refreshValue();break;case"value":this._animateOff=!0,this._refreshValue(),this._change(null,0),this._animateOff=!1;break;case"values":for(this._animateOff=!0,this._refreshValue(),i=0;valsLength>i;i+=1)this._change(null,i);this._animateOff=!1;break;case"min":case"max":this._animateOff=!0,this._refreshValue(),this._animateOff=!1;break;case"range":this._animateOff=!0,this._refresh(),this._animateOff=!1}},_value:function(){var val=this.options.value;return val=this._trimAlignValue(val)},_values:function(index){var val,vals,i;if(arguments.length)return val=this.options.values[index],val=this._trimAlignValue(val);if(this.options.values&&this.options.values.length){for(vals=this.options.values.slice(),i=0;i<vals.length;i+=1)vals[i]=this._trimAlignValue(vals[i]);return vals}return[]},_trimAlignValue:function(val){if(val<=this._valueMin())return this._valueMin();if(val>=this._valueMax())return this._valueMax();var step=this.options.step>0?this.options.step:1,valModStep=(val-this._valueMin())%step,alignValue=val-valModStep;return 2*Math.abs(valModStep)>=step&&(alignValue+=valModStep>0?step:-step),parseFloat(alignValue.toFixed(5))},_valueMin:function(){return this.options.min},_valueMax:function(){return this.options.max},_refreshValue:function(){var lastValPercent,valPercent,value,valueMin,valueMax,oRange=this.options.range,o=this.options,that=this,animate=this._animateOff?!1:o.animate,_set={};this.options.values&&this.options.values.length?this.handles.each(function(i){valPercent=(that.values(i)-that._valueMin())/(that._valueMax()-that._valueMin())*100,_set["horizontal"===that.orientation?"left":"bottom"]=valPercent+"%",$(this).stop(1,1)[animate?"animate":"css"](_set,o.animate),that.options.range===!0&&("horizontal"===that.orientation?(0===i&&that.range.stop(1,1)[animate?"animate":"css"]({left:valPercent+"%"},o.animate),1===i&&that.range[animate?"animate":"css"]({width:valPercent-lastValPercent+"%"},{queue:!1,duration:o.animate})):(0===i&&that.range.stop(1,1)[animate?"animate":"css"]({bottom:valPercent+"%"},o.animate),1===i&&that.range[animate?"animate":"css"]({height:valPercent-lastValPercent+"%"},{queue:!1,duration:o.animate}))),lastValPercent=valPercent}):(value=this.value(),valueMin=this._valueMin(),valueMax=this._valueMax(),valPercent=valueMax!==valueMin?(value-valueMin)/(valueMax-valueMin)*100:0,_set["horizontal"===this.orientation?"left":"bottom"]=valPercent+"%",this.handle.stop(1,1)[animate?"animate":"css"](_set,o.animate),"min"===oRange&&"horizontal"===this.orientation&&this.range.stop(1,1)[animate?"animate":"css"]({width:valPercent+"%"},o.animate),"max"===oRange&&"horizontal"===this.orientation&&this.range[animate?"animate":"css"]({width:100-valPercent+"%"},{queue:!1,duration:o.animate}),"min"===oRange&&"vertical"===this.orientation&&this.range.stop(1,1)[animate?"animate":"css"]({height:valPercent+"%"},o.animate),"max"===oRange&&"vertical"===this.orientation&&this.range[animate?"animate":"css"]({height:100-valPercent+"%"},{queue:!1,duration:o.animate}))},_handleEvents:{keydown:function(event){var allowed,curVal,newVal,step,index=$(event.target).data("ui-slider-handle-index");switch(event.keyCode){case $.ui.keyCode.HOME:case $.ui.keyCode.END:case $.ui.keyCode.PAGE_UP:case $.ui.keyCode.PAGE_DOWN:case $.ui.keyCode.UP:case $.ui.keyCode.RIGHT:case $.ui.keyCode.DOWN:case $.ui.keyCode.LEFT:if(event.preventDefault(),!this._keySliding&&(this._keySliding=!0,$(event.target).addClass("ui-state-active"),allowed=this._start(event,index),allowed===!1))return}switch(step=this.options.step,curVal=newVal=this.options.values&&this.options.values.length?this.values(index):this.value(),event.keyCode){case $.ui.keyCode.HOME:newVal=this._valueMin();break;case $.ui.keyCode.END:newVal=this._valueMax();break;case $.ui.keyCode.PAGE_UP:newVal=this._trimAlignValue(curVal+(this._valueMax()-this._valueMin())/numPages);break;case $.ui.keyCode.PAGE_DOWN:newVal=this._trimAlignValue(curVal-(this._valueMax()-this._valueMin())/numPages);break;case $.ui.keyCode.UP:case $.ui.keyCode.RIGHT:if(curVal===this._valueMax())return;newVal=this._trimAlignValue(curVal+step);break;case $.ui.keyCode.DOWN:case $.ui.keyCode.LEFT:if(curVal===this._valueMin())return;newVal=this._trimAlignValue(curVal-step)}this._slide(event,index,newVal)},click:function(event){event.preventDefault()},keyup:function(event){var index=$(event.target).data("ui-slider-handle-index");this._keySliding&&(this._keySliding=!1,this._stop(event,index),this._change(event,index),$(event.target).removeClass("ui-state-active"))}}})}(jQuery);
