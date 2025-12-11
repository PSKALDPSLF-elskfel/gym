import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class GeneratePassword {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        
        // 生成密码 123456 的加密版本
        String password = "123456";
        String encodedPassword = encoder.encode(password);
        
        System.out.println("原始密码: " + password);
        System.out.println("加密后密码: " + encodedPassword);
        
        // 验证密码
        boolean matches = encoder.matches(password, encodedPassword);
        System.out.println("密码验证: " + matches);
        
        // 验证数据库中的密码
        String dbPassword = "$2a$10$0EB8hAzCT25cUhNeXDOhkujD./0TYjjiENnpirImJg4q4MeE/pKPa";
        boolean dbMatches = encoder.matches(password, dbPassword);
        System.out.println("数据库密码验证: " + dbMatches);
    }
}
