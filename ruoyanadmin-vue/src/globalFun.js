import Vue from "vue";

Vue.mixin({
    methods: {
        hasAuth(perm) {
            var authority = this.$store.state.menus.permList

            console.log(this.$store.state.menus)
            console.log("权限内容")
            console.log(authority)

            return authority.indexOf(perm) > -1;
        }
    }
})