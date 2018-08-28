Vue.component('v-select', VueSelect.VueSelect);

new Vue({
    el: '#app',
    data: function () {
        return {
            list: [],
        };
    },
    watch: {},
    methods: {
        queryList: function () {
            var _this = this;
            http.get("/qCode/list", {},
            function (resp) {
                var list = [];
                _.each(resp, function(v){
                    list.push(JSON.parse(v))
                })
                _this.list = list;
            })
        }
    },
    mounted: function () {
        this.queryList()
        setInterval(this.queryList, 1000)
    }
})
