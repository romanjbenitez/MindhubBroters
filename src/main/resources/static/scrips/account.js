Vue.createApp({
  data() {
    return {
      firstName: "",
      account: [],
      numbreOfAccount: "",
      accountID: "",
      accounts: [],
      hide: "hide",
      urlAccount: "http://localhost:8080/web/account.html?id=",
      balance: 0,
      day: 0,
      month: "",
      year: 0,
      transactions: [],
      userProfille: null,
      charging: true,
      accountBalance : 0

    };
  },

  created() {
    const urlParams = new URLSearchParams(window.location.search);
    const paramsID = urlParams.get("id");
    axios
      .get(`http://localhost:8080/api/accounts/${paramsID}`)
      .then((api) => {
        this.account = api.data;
        this.numbreOfAccount = this.account.number;
        this.accountID = this.account.id;
        this.transactions = this.account.transactions;
        this.accountBalance = this.account.balance;
      })
      .catch((err) => console.log(err));
    axios
      .get("http://localhost:8080/api/clients/current")
      .then((api) => {
        this.firstName = api.data.firstName;
        this.accounts = api.data.accounts.sort((a, b) => a.id - b.id);
        this.balance = this.accounts
          .filter((account) => account.balance != 0)
          .reduce((acc, item) => {
            return acc + item.balance;
          }, 0);
        this.userProfille =
          api.data.imgProfile == null ? null : "../assets/usersProfiles/" + api.data.imgProfile;
        setTimeout(() => { this.charging = false }, 500)

      })
      .catch((err) => console / log(err));
  },

  methods: {
    hideBalance() {
      this.hide == "hide" ? (this.hide = "show") : (this.hide = "hide");
    },
    addAccount() {
      axios
        .post("/api/clients/current/accounts", {
          headers: { "content-type": "application/x-www-form-urlencoded" },
        })
        .then((response) => {
          console.log("created");
        })
        .catch((err) => console.log(err));
      axios
        .get("http://localhost:8080/api/clients/current")
        .then(
          (api) =>
            (this.accounts = api.data.accounts.sort((a, b) => a.id - b.id))
        );
    },
    logout() {
      axios
        .post("/api/logout")
        .then((response) => window.location.replace("./index.html"));
    },
    addAccount() {
      Swal.fire({
        title: 'Do you want add a new account?',
        showDenyButton: true,
        confirmButtonText: 'Add new Account',
        denyButtonText: `Don't add`,
      }).then((result) => {

        if (result.isConfirmed) {
          axios
            .post("/api/clients/current/accounts", {
              headers: { "content-type": "application/x-www-form-urlencoded" },
            })
            .then((response) => {
              axios
                .get("http://localhost:8080/api/clients/current")
                .then(
                  (api) =>
                    (this.accounts = api.data.accounts.sort((a, b) => a.id - b.id))
                );
              Swal.fire("Account created successfully", '', 'success')
            })
            .catch((err) => Swal.fire("You don't could have more than three account", '', 'info')
            );
        } else if (result.isDenied) {
          Swal.fire('Action canceled')
        }
      })
    },
    deleteAccount() {
      if(this.accountBalance !== 0){
        return Swal.fire("first you must empty the balance of your account", '', 'info')
      }

      Swal.fire({
        title: `Are you sure you want to delete the account number ${this.numbreOfAccount}?`,
        showDenyButton: true,
        confirmButtonText: 'Yes, delete',
        denyButtonText: `Don't delete`,
      }).then((result) => {
        if (result.isConfirmed) {
          axios.post("/api/clients/current/accounts/delete", `number=${this.numbreOfAccount}`, { headers: { "content-type": "application/x-www-form-urlencoded" } },).then(res => {
            Swal.fire("Account deleted successfully", '', 'success')
            setTimeout(() => {
              window.location.replace("./accounts.html")
            }, 1000)
          })
        }
      })

    }


  },

  computed: {
    getToday() {
      let date = new Date();
      this.day = date.getDate();
      this.year = date.getFullYear();
      this.month = date.toDateString().split(" ")[1];
    },
  },
}).mount("#app");
