package de.fraunhofer.abm.zenodo;

/**
 * Created by benhermann on 31.05.17.
 */
public final class API {
    private API() {}

    public final class Deposit {
        private Deposit() {}

        public final static String Depositions = "api/deposit/depositions";
        public final static String DepositionsSecure = "api/deposit/depositions?access_token={access_token}";

        public final static String Files = "api/deposit/depositions/{id}/files";
        public final static String Publish = "api/deposit/depositions/{id}/actions/publish";
        public final static String Discard = "api/deposit/depositions/{id}/actions/discard";
        public final static String Edit = "api/deposit/depositions/{id}/actions/edit";
        public final static String NewVersion = "/api/deposit/depositions/{id}/actions/newversion";
        public final static String Entity = "api/deposit/depositions/{id}";

    }
}

