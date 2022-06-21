Vue.createApp({
  data() {
    return {
      urlAccount: "https://mhb-online-banking.herokuapp.com/web/account.html?id=",
      firstName: "",
      lastName: "",
      email: "",
      accounts: [],
      hide: "hide",
      balance: 0,
      userProfille: null,
      edit: true,
      newFirstName: "",
      newLastName: "",
      newEmail: "",
    };
  },

  created() {
    axios
      .get("/api/clients/current")
      .then((api) => {
        this.firstName = api.data.firstName;
        this.accounts = api.data.accounts.sort((a, b) => a.id - b.id);
        this.lastName = api.data.lastName;
        this.email = api.data.email;
        this.balance = this.accounts
          .filter((account) => account.balance != 0)
          .reduce((acc, item) => {
            return acc + item.balance;
          }, 0);
        this.userProfille =
          api.data.imgProfile == null
            ? null
            : "../assets/usersProfiles/" + api.data.imgProfile;
        this.newEmail = api.data.email;
        this.newLastName = api.data.lastName;
        this.newFirstName = api.data.firstName;
      })
      .catch((err) => console.log(err));
  },

  methods: {
    hideBalance() {
      this.hide == "hide" ? (this.hide = "show") : (this.hide = "hide");
    },
    editClass() {
      this.edit = this.edit ? false : true;
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
    logout() {
      axios
        .post("/api/logout")
        .then((response) => window.location.replace("./index.html"));
    },

    getImg() {
      let doc = document.querySelector("#filechooser");
      const data = new FormData();
      data.append("file", doc.files[0]);
      const config = { headers: { "Content-Type": "multipart/form-data" } };
      axios
        .post("/api/clients/settings", data, config)
        .then((res) => {
          axios.get("/api/clients/current").then((api) => {
            this.userProfille =
              "../assets/usersProfiles/" + api.data.imgProfile;
          });
        })
        .catch((err) => console.log(err));
    },
    saveChanges() {
      axios.patch("/api/clients/current/update", `firstName=${this.newFirstName}&lastName=${this.newLastName}`, { headers: { "content-type": "application/x-www-form-urlencoded" } }).then(res => {
        console.log("success")
      })
    },
  },
  computed: {},
}).mount("#app");
