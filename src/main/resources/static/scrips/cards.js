Vue.createApp({
    data() {
      return {
        urlAccount: "https://mhb-online-banking.herokuapp.com/web/account.html?id=",
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
        userProfille: null,
        charging : true,
        clientRole: ""
        
      };
    },
  
    created() {
      axios
        .get("/api/clients/current")
        .then((api) => {
          this.firstName = api.data.firstName;
          this.clientRole = api.data.clientRole
          this.accounts = api.data.accounts.sort((a, b) => a.id - b.id)
          this.creditsCard = api.data.cards.filter(card => card.type == 'CREDIT').filter(card=> card.hidden == false)
          this.debitsCard = api.data.cards.filter(card => card.type == "DEBIT").filter(card=> card.hidden == false)
          this.lastName = api.data.lastName;
          this.balance = this.accounts
          .filter((account) => account.balance != 0)
          .reduce((acc, item) => {
            return acc + item.balance;
          }, 0);
          this.userProfille =
          api.data.imgProfile == null ? null : "../assets/usersProfiles/" + api.data.imgProfile;
          setTimeout(() => {this.charging = false}, 500)
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
      deleteCard(){
        window.location.replace("./delete-cards.html")
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
  