+function($, app) { "use strict";
	app = app || {};
	app.initOptions  = function (){//JQuery validation 兼容  Bootstrap输入框图标样式
		app.options = {      
			highlight: function(element) {
			$(element).closest('.form-group').addClass('has-error');
		  },
		  unhighlight: function(element) {
			$(element).closest('.form-group').removeClass('has-error');
		  },
		  errorElement: 'span',
		  errorClass: 'help-block',
		  errorPlacement: function(error, element) {
			if(element.parent('.input-group').length) {
			  error.insertAfter(element.parent());
			} else {
			  error.insertAfter(element);
			}
		  }
		};
	}
}(window.jQuery, window.app);

+

+function($, app) { "use strict"; 
	app = app || {};
	app.openL = function(title,content) {//bootstrap样式弹窗
	  $('#alert-modal-title').html(title);
	  $('#alert-modal-body').html(content);
	  return $('#alert-modal').modal('show');
  }
}(window.jQuery, window.app);
