package pl.ppiwd.exerciseanalyst.common.auth;

public class SignUpData {
    public enum Gender {
        Male,
        Female,
    }

    final String email;
    final String password;
    final int heightInCm;
    final double weightInKg;
    final int age;
    final String gender;

    public SignUpData(
            String email,
            String password,
            int heightInCm,
            double weightInKg,
            int age,
            String gender) {
        this.email = email;
        this.password = password;
        this.heightInCm = heightInCm;
        this.weightInKg = weightInKg;
        this.age = age;
        this.gender = gender;
    }
}
