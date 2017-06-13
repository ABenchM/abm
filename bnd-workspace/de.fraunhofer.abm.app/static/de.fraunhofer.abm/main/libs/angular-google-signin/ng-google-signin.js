/*! ng-google-signin - v0.0.1 2015-08-27 */
/**
 * google-signin module
 */
angular.module("google-signin", []).provider("GoogleSignin", [ function() {
    /**
     * Options object available for module
     * options/services definition.
     * @type {Object}
     */
    var a = {};
    /**
     * Sets the client id.
     * @param {string} clientId the client id
     * @returns {*} a chainable reference
     */
    this.setClientId = function(b) {
        a.client_id = b;
        return this;
    };
    /**
     * Gets the client id.
     * @returns {string|*|undefined} the client id
     */
    this.getClientId = function() {
        return a.client_id;
    };
    /**
     * Sets the cookie policy
     * @param {string} cookiePolicy the cookiepolicy
     * @returns {*} a chainable reference
     */
    this.setCookiePolicy = function(b) {
        a.cookie_policiy = b;
        return this;
    };
    /**
     * Gets the cookie policy
     * @returns {string|*|undefined} the cookie policy
     */
    this.getCookiePolicy = function() {
        return a.cookie_policiy;
    };
    /**
     * Sets the basic profile
     * @param {string} fetchBasicProfile the fetch basic profile option
     * @returns {*} a chainable reference
     */
    this.setFetchBasicProfile = function(b) {
        a.fetch_basic_profile = b;
        return this;
    };
    /**
     * Gets the fetch basic profile option
     * @returns {string|*|undefined} the fetch basic profile option
     */
    this.getFetchBasicProfile = function() {
        return a.fetch_basic_profile;
    };
    /**
     * Sets the hosted domain
     * @param {string} hostedDomain the hosted domain
     * @returns {*} a chainable reference
     */
    this.setHostedDomain = function(b) {
        a.hosted_domain = b;
        return this;
    };
    /**
     * Gets the hosted domain
     * @returns {string|*|undefined} the hosted domain
     */
    this.getHostedDomain = function() {
        return a.hosted_domain;
    };
    /**
     * Sets the OpenID Realm
     * @param {string} openIDRealm the OpenID realm to set
     * @returns {*} a chainable reference
     */
    this.setOpenIDRealm = function(b) {
        a.openid_realm = b;
        return this;
    };
    /**
     * Gets the OpenID Realm
     * @returns {string|*|undefined} the OpenID Realm
     */
    this.getOpenIDRealm = function() {
        return a.openid_realm;
    };
    /**
     * Scopes
     * @default ['profile', 'email']
     */
    a.scopes = [ "profile", "email" ];
    /**
     * Sets current scopes
     * @param {string[]} scopes the scope to set
     * @returns {*} a chainable reference
     */
    this.setScopes = function(b) {
        a.scopes = b;
        return this;
    };
    /**
     * Gets the current scopes
     * @returns {Array|*|Boolean} the scopes array
     */
    this.getScopes = function() {
        return a.scopes;
    };
    /**
     * Init Google Plus API
     */
    this.init = function(b) {
        angular.extend(a, b);
    };
    /**
     * This defines the Google SignIn Service on run.
     */
    this.$get = [ "$rootScope", "$q", function(b, c) {
        var d;
        /**
       * NgGoogle Class
       * Wraps most of the functionality of the Google Sign-In JavaScript
       * SDK found at
       * https://developers.google.com/identity/sign-in/web/reference
       * @type {Class}
       */
        var e = function() {};
        /**
       * Signs in the current user to the app.
       * See {@link https://developers.google.com/identity/sign-in/web/reference#googleauthsignin Google Reference} for more details.
       * @param {} [loginOptions] the options to configure login with
       * @returns {Function|promise}
       */
        e.prototype.signIn = function(a) {
            return g(d.signIn(a));
        };
        /**
       * Signs out the current user from the app.
       * See {@link https://developers.google.com/identity/sign-in/web/reference#googleauthsignout Google Reference} for more details.
       * @returns {Function|promise} Fulfilled when the user has been signed
       * out.
       */
        e.prototype.signOut = function() {
            return g(d.signOut());
        };
        /**
       * Prompts the user to grant offline access for the app.
       * See {@link https://developers.google.com/identity/sign-in/web/reference#googleauthgrantofflineaccesswzxhzdk74optionswzxhzdk75 Google Reference} for more details.
       * @param {} [options] the options to confgiure offline access with
       * @returns {Function|promise}
       */
        e.prototype.grantOfflineAccess = function(a) {
            return g(d.grantOfflineAccess(a));
        };
        /**
       * Returns the user's sign in status.
       * See {@link https://developers.google.com/identity/sign-in/web/reference#googleauthissignedinget Google Reference} for more details.
       * @returns {boolean}
       */
        e.prototype.isSignedIn = function() {
            return d.isSignedIn.get();
        };
        /**
       * Gets the current user.
       * See {@link https://developers.google.com/identity/sign-in/web/reference#googleauthcurrentuserget Google Reference} for more details.
       * @returns {*} GoogleUser object
       */
        e.prototype.getUser = function() {
            return d.currentUser.get();
        };
        /**
       * Gets the basic profile for the current user.
       * See {@link https://developers.google.com/identity/sign-in/web/reference#googleusergetbasicprofile Google Reference} for more details.
       * @returns {*} GoogleUser profile
       */
        e.prototype.getBasicProfile = function() {
            var a = this.getUser().getBasicProfile();
            var b = null;
            if (a) {
                b = {
                    id: a.getId(),
                    name: a.getName(),
                    image: a.getImageUrl(),
                    email: a.getEmail()
                };
            }
            return b;
        };
        /**
       * Disconnects the current user from the app.
       * See {@link https://developers.google.com/identity/sign-in/web/reference#googleauthdisconnect Google Reference} for more details.
       */
        e.prototype.disconnect = function() {
            d.disconnect();
        };
        /**
       * This callback handles the onload callback for the GAPI lib
       * @private
       */
        e.prototype._loadCallback = function() {
            gapi.load("auth2", f);
        };
        return new e();
        /**
       * Initialization callback called after GAPI is loaded.
       * @private
       */
        function f() {
            d = gapi.auth2.init(a);
            d.currentUser.listen(function(a) {
                b.$broadcast("ng-google-signin:currentUser", a);
                b.$apply();
            });
            d.isSignedIn.listen(function(a) {
                b.$broadcast("ng-google-signin:isSignedIn", a);
                b.$apply();
            });
        }
        /**
       * Wraps a googleThenable into an Angular promise
       * @param googleThenable the googleThenable
       * @returns {Function|promise} the $q promise
       * @private
       */
        function g(a) {
            var b = c.defer();
            a.then(b.resolve, b.reject);
            return b.promise;
        }
    } ];
} ]).run([ "$window", "GoogleSignin", function(a, b) {
    // This needs to be on the window for the callback
    a._startGoogleSignin = b._loadCallback;
    var c = document.createElement("script");
    c.type = "text/javascript";
    c.async = true;
    c.src = "https://apis.google.com/js/client:platform.js?onload=_startGoogleSignin";
    var d = document.getElementsByTagName("script")[0];
    d.parentNode.insertBefore(c, d);
} ]);