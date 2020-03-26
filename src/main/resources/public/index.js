Vue.component('v-select', VueSelect.VueSelect);
var loginDialog;
var loginCallBack;
function openEdit(cb) {
	$('#loginDialog').modal({});
	loginCallBack = cb;
}

new Vue({
	el : '#app',
	data : function() {
		return {
			linkList : linkList,
			serviceId : '',
			serviceList : [],
			configList : [],
			config : {},
			dialogError : null,
			editingConfig : null
		};
	},
	methods : {
		listServiceIds : function() {
			var _this = this;
			http.get("/zk/listServiceIds", {

			}, function(resp) {
				_this.serviceList = resp.content;
			})
		},
		selectService : function() {

		},
		add : function() {
			this.config = {};
			openEdit();
		},
		edit : function(serviceId) {
			var _this = this;
			_this.dialogError = null;
			http.get("/zk/get", {
				serviceId : serviceId
			}, function(resp) {
				if (resp) {
					_this.config = resp.content
				} else {
					_this.config = {};
				}
				openEdit();
			})

		},
		save : function(config) {
			var _this = this;
			if (!config.serviceId || !config.context) {
				_this.dialogError = '服务名和内容都不能为空！';
				return;
			}
			
			var url = "/zk/save";
			var isAdd = true;
			if (config.updateDate) {
				url = "/zk/update";
				isAdd = false;
			}
			http.post(url, config, function(resp) {
				if (isAdd) {
					_this.serviceList.push(config.serviceId);
				}
				$('#loginDialog').modal('hide');
			}, function(err) {
				_this.dialogError = '系统出错!';
			})
		},
		del : function(serviceId, index) {
			var _this = this;
			_this.error = null;
			if (confirm("确认删除")) {
				http.get("/zk/delete", {
					serviceId : serviceId
				}, function(data) {
					_this.serviceList.splice(index, 1);
				}, function(err) {
					alert("系统出错！");
				})
			}
		}
	},
	mounted : function() {
		this.listServiceIds();
	}
})
