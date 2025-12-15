import axios from "axios";

export default class User {
    name = "";
    authenticated = false;
    axios = null;
    token = "";

    initAxios(token = "") {
        let headers = {
            'Content-Type': 'application/json',
        };
        if (token) {
            console.log("Init axios with token");
            headers['Authorization'] = 'Bearer ' + token;
        }
        this.axios = axios.create({
            baseURL: 'http://localhost:8080/secu-users/',
            timeout: 2000,
            headers: headers,
        });
    }

    constructor() {
        const savedToken = sessionStorage.getItem("jwtToken");
        const savedName = sessionStorage.getItem("username");

        if (savedToken && savedName) {
            this.name = savedName;
            this.token = savedToken;
            this.authenticated = true;
            this.initAxios(savedToken);
        } else {
            this.logout();
            this.initAxios();
        }
    }

    async login(name, password) {
        this.logout();
        const config = {
            params: { username: name, password: password }
        };

        try {
            const r = await this.axios.get("login", config);
            const token = r.data;

            sessionStorage.setItem("jwtToken", token);
            sessionStorage.setItem("username", name);

            this.name = name;
            this.token = token;
            this.authenticated = true;
            this.initAxios(token);

            return true;
        } catch (e) {
            return false;
        }
    }

    logout() {
        this.name = "";
        this.token = "";
        this.authenticated = false;
        sessionStorage.removeItem("jwtToken");
        sessionStorage.removeItem("username");
        this.initAxios();
    }

    async whoami() {
        let result = await this.axios.get("me");
        return result.data;
    }
}
