Vue.createApp({
  data() {
    return {
      firstName: "",
      account: [],
      numbreOfAccount: "",
      accountID: "",
      accounts: [],
      hide: "hide",
      urlAccount: "https://mhb-online-banking.herokuapp.com/web/account.html?id=",
      balance: 0,
      day: 0,
      month: "",
      year: 0,
      transactions: [],
      userProfille: null,
      charging: true,
      accountBalance : 0,
      clientRole: "",
      from: "",
      to: "",
      filterTransaction : [],
    };
  },

  created() {
    const urlParams = new URLSearchParams(window.location.search);
    const paramsID = urlParams.get("id");
    axios
      .get(`/api/accounts/${paramsID}`)
      .then((api) => {
        this.account = api.data;
        this.numbreOfAccount = this.account.number;
        this.accountID = this.account.id;
        this.transactions = this.account.transactions;
        this.filterTransaction = this.transactions;
        this.accountBalance = this.account.balance;
      })
      .catch((err) => console.log(err));
    axios
      .get("/api/clients/current")
      .then((api) => {
        this.firstName = api.data.firstName;
        this.clientRole = api.data.clientRole
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
        .get("/api/clients/current")
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
    async addAccount() {

      const { value: accountType } = await Swal.fire({
        title: 'Select an account type',
        input: 'select',
        inputOptions: {
          'Saving': 'Saving',
          'Checking': 'Checking'
        },
        inputPlaceholder: 'Select an account type',
        showCancelButton: true,
      });
      if (accountType) {
        Swal.fire({
          title: 'Do you want add a new account?',
          showDenyButton: true,
          confirmButtonText: 'Add new Account',
          denyButtonText: `Don't add`,
        }).then((result) => {
          if (result.isConfirmed) {
            axios
              .post("/api/clients/current/accounts", `accountType=${accountType}`, {
                headers: { "content-type": "application/x-www-form-urlencoded" },
              })
              .then((response) => {
                axios
                  .get("/api/clients/current")
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
      }
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

    },
    formatDate(date) {
      let newDate = date.split("T")[0];
      let time = date.split("T")[1].split(".")[0];
      return newDate + " at " + time;
    },
    downloadPDF(){

      const urlParams = new URLSearchParams(window.location.search)
      
      let id = urlParams.get("id").toString();
      let from1 = this.from.split("T")[0].split("-").reverse().join("-")
      let from2 = this.from.split("T")[1]
      let finalFrom = from1 + " " + from2
      let to1 = this.to.split("T")[0].split("-").reverse().join("-")
      let to2 = this.to.split("T")[1]
      let finalTo = to1 + " " + to2

      axios.post("/api/pdf/account",`from=${finalFrom}&to=${finalTo}&id=${id}`,
      { responseType: 'blob' }, 
      {headers:{"Content-type": "application/pdf" }})
      .then((res) => {
        let fileURL = window.URL.createObjectURL(new Blob([res.data]));
        let fileLink = document.createElement('a');
        fileLink.href = fileURL;
        fileLink.setAttribute('download', `receiptAccountNumber${this.numbreOfAccount}.pdf`);
        document.body.appendChild(fileLink);
        fileLink.click();
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
    filterTrasactions(){
      if(this.to !== "" && this.from !== ""){
        let from = new Date(this.from)
        let to = new Date(this.to.replace(this.to.slice(9), parseInt(this.to.slice(9))+1))
        console.log(this.from)
        console.log(this.to)
        this.filterTransaction = this.transactions.filter((transaction) => {
          let transactionDate = new Date(transaction.date);
          return transactionDate >= from && transactionDate <= to
        })
      }else{
        this.filterTransaction = this.transactions;
      }
    }

  },
}).mount("#app");
