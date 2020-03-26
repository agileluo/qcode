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
			users: [],
			user : {},
			dialogError : null,
			editUser: null
		};
	},
	methods : {
		listUsers : function() {
			var _this = this;
			http.get("./user/list", {

			}, function(resp) {
				_this.users = resp.content;
			})
		},
		add : function() {
			this.user = {};
			openEdit();
		},
		edit : function(u) {
			var _this = this;
			_this.editUser = u;
			_this.dialogError = null;
			http.get("./user/getById", {
				id : u.id
			}, function(resp) {
				if (resp) {
					_this.user = resp.content
				} else {
					_this.user = {};
				}
				openEdit();
			})

		},
		save : function(user) {
			var _this = this;
			if (!user.userName || !user.password || !user.realName) {
				_this.dialogError = '用户名、密码和真实姓名不能为空都不能为空！';
				return;
			}
			
			var url = "./user/add";
			var isAdd = true;
			if (user.id) {
				url = "./user/update";
				isAdd = false;
			}
			http.post(url, user, function(resp) {
				if (isAdd) {
					_this.users.push(resp.content);
				} else {
                    _this.listUsers();
				}
				$('#loginDialog').modal('hide');
			}, function(err) {
				_this.dialogError = '系统出错!';
			})
		},
		del : function(u, index) {
			var _this = this;
			_this.error = null;
			if (confirm("确认删除")) {
				http.get("./user/delete", {
					id : u.id
				}, function(data) {
					_this.users.splice(index, 1);
				}, function(err) {
					alert("系统出错！");
				})
			}
		}
	},
	mounted : function() {
		this.listUsers();
	}
})
