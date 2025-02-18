package u5w2d5.etm.auth.model;

public enum AppUserRole {
    ROLE_ADMIN,
    ROLE_USER,
    ROLE_SELLER,
    ROLE_BUYER;

    @Override
    public String toString() {
        switch (this) {
            case ROLE_ADMIN:
                return "Administrator";
            case ROLE_USER:
                return "User";
            case ROLE_SELLER:
                return "Seller";
            case ROLE_BUYER:
                return "Buyer";
            default:
                return super.toString();
        }
    }

    public static AppUserRole fromString(String role) {
        switch (role.toLowerCase()) {
            case "administrator":
                return ROLE_ADMIN;
            case "user":
                return ROLE_USER;
            case "seller":
                return ROLE_SELLER;
            case "buyer":
                return ROLE_BUYER;
            default:
                throw new IllegalArgumentException("Unknown role: " + role);
        }
    }

}
