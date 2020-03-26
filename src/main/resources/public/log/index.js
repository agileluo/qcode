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
			req: {
                qrcode: null,
                createTimeStart: null,
                createTimeEnd: null
			},
            downUrl: '/log/downLoad',
			total: 0,
			pageSize: 10,
			page: 1,
			pageQuery: {},
            linkList : linkList,
			list: []
		};
	},
	methods : {
		query : function(page) {
			var _this = this;
			http.post("/log/pageQuery", {
				page: page ? page : 1,
				pageSize: _this.pageSize,
				data: _this.req
			}, function(resp) {
				_this.list = resp.content.items;
				_this.total = resp.content.total
			})
		},
		add : function() {
			this.user = {};
			openEdit();
		},
		del : function(u, index) {
			var _this = this;
			_this.error = null;
			if (confirm("确认删除")) {
				http.get("/log/delete", {
					id : u.id
				}, function(data) {
					_this.list.splice(index, 1);
				}, function(err) {
					alert("系统出错！");
				})
			}
		},
        goPage: function(page){
            this.query(page);
        },
        genDownUrl: function(){
			var baseUrl = '/log/downLoad';
			baseUrl += '?qrcode=' + (this.req.qrcode ? this.req.qrcode : '');
            baseUrl += '&createTimeStart=' + (this.req.createTimeStart ? this.req.createTimeStart : '');
            baseUrl += '&createTimeEnd=' + (this.req.createTimeEnd ? this.req.createTimeEnd : '');
            this.downUrl = baseUrl;
		}
	},
	mounted : function() {
		this.query();
        this.$watch('req.qrcode', (newVal, oldVal) => {this.genDownUrl()})
        this.$watch('req.createTimeStart', (newVal, oldVal) => {this.genDownUrl()})
        this.$watch('req.createTimeEnd', (newVal, oldVal) => {this.genDownUrl()})
	},
    components: {
        "pagination": Pagination
    }
})
