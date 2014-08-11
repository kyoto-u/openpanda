var osp = osp || {};
osp.bag = osp.bag || {};

(function ($, osp) {   
    osp.editor = function(selector, field, url, params) {
        var that = {};
        
        that.selector = selector;
        that.field = field;
        that.url = url ? url : '';
        that.params = params || {};

        var init = function() {
			$(that.selector + ' .editLink').click(function(ev) {
				ev.preventDefault();
				that.showEdit();
			});
			
			$(that.selector + ' .saveLink').click(function(ev) {
				ev.preventDefault();
				var data = $(that.selector + ' .inlineEdit').val();
				if (data != $(that.selector + ' .editableText').text()) {
					$(that.selector + ' .editableText').text(data);
					var params = {};
					$.extend(params, that.params);
					params[that.field] = data;
					$.post(that.url, params);
				}
				that.hideEdit();
			});
			
			$(that.selector + ' .undoLink').click(function(ev) {
				ev.preventDefault();
				that.hideEdit();
			});
        };

		that.showEdit = function() {
			$(that.selector + ' .inlineEdit').val($(that.selector + ' .editableText').text());
			$(that.selector + ' .saveLink').show();
			$(that.selector + ' .undoLink').show();
			$(that.selector + ' .editLink').hide();
			$(that.selector + ' .inlineEdit').show();
			$(that.selector + ' .inlineEdit').focus();
			$(that.selector + ' .editableText').hide();
			$(that.selector).addClass('editableActive');
			resizeFrame('grow')
		}	
	
		that.hideEdit = function() {
			$(that.selector + ' .saveLink').hide();
			$(that.selector + ' .undoLink').hide();
			$(that.selector + ' .editLink').show();
			$(that.selector + ' .inlineEdit').hide();
			$(that.selector + ' .editableText').show();
			$(that.selector + ' .inlineEdit').val('');		
			$(that.selector).removeClass('editableActive');
			resizeFrame('shrink')
		}
		
		init();
        return that;
    };
})(jQuery, osp);
