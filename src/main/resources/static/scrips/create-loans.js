Vue.createApp({
    data() {
        return {
            loans: [],
            name: "",
            maxAmount: "",
            interestPercentage: "",
            payments: "",
        };
    },

    created() {
        axios
            .get("/api/loans").then((api) => {
                this.loans = api.data
                console.log(this.loans)
            }).catch(err => console.log(err))
    },

    methods: {
        createLoan() {
            axios.post("/api/loans/admin", `name=${this.name}&maxAmount=${this.maxAmount}&payments=${this.payments}&interestPercentage=${this.interestPercentage}`,
                {
                    headers: { "content-type": "application/x-www-form-urlencoded" },
                }).then((res) => {
                    axios
                        .get("/api/loans").then((api) => {
                            this.loans = api.data
                        }).catch(err => console.log(err))
                }).catch(err => console.log(err))
        },

    },

    computed: {

    },
}).mount("#app");
