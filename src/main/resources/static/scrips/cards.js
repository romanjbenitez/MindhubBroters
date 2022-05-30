Vue.createApp({
    data() {
      return {
        firstName: "",
        lastName: "",
        accounts: [],
        hide: "hide",
        balance: 0,
        day: 0,
        month: "",
        year: 0,
        transactions: [],
        creditsCard : [],
        debitsCard : [],
      };
    },
  
    created() {
      axios
        .get("http://localhost:8080/api/clients/current")
        .then((api) => {
          this.firstName = api.data.firstName;
          this.accounts = api.data.accounts.sort((a, b) => a.id - b.id)
          this.creditsCard = api.data.cards.filter(card => card.type == 'CREDIT')
          this.debitsCard = api.data.cards.filter(card => card.type == "DEBIT")
          this.lastName = api.data.lastName;
          this.balance = this.accounts
          .filter((account) => account.balance != 0)
          .reduce((acc, item) => {
            return acc + item.balance;
          }, 0);
        })
        .catch((err) => console / log(err));
    },
  
    methods: {
      hideBalance() {
        this.hide == "hide" ? (this.hide = "show") : (this.hide = "hide");
      },
      addcard(){
        window.location.replace("./create-cards.html")
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
      logout(){
        axios.post('/api/logout').then(response => window.location.replace("./index.html"))
      }
    },
  
    computed: {
      getToday() {
        let date = new Date();
        this.day = date.getDate();
        this.year = date.getFullYear();
        this.month = date.toDateString().split(" ")[1]
      },
    },
  }).mount("#app");
  