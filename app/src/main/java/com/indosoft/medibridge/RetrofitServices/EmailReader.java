package com.indosoft.medibridge.RetrofitServices;


public class EmailReader {

//    public String getOtpFromEmail(String email, String password) {
//        String otp = null;
//
//        try {
//            Properties properties = new Properties();
//            properties.put("mail.store.protocol", "imaps");
//            properties.put("mail.imaps.host", "imap.gmail.com");
//            properties.put("mail.imaps.port", "993");
//            properties.put("mail.imaps.ssl.enable", "true");
//
//            // Connect to the mail server
//            Session emailSession = Session.ge(properties);
//            Store store = emailSession.getStore("imaps");
//            store.connect("imap.gmail.com", email, password);
//
//            // Open inbox folder
//            Folder inbox = store.getFolder("INBOX");
//            inbox.open(Folder.READ_ONLY);
//
//            // Get the most recent message
//            Message[] messages = inbox.getMessages();
//            for (Message message : messages) {
//                // Look for OTP pattern in the subject or body
//                String subject = message.getSubject();
//                String content = (String) message.getContent();
//
//                // Search for the OTP, assuming it's in the subject or body (you can adjust this based on your email format)
//                if (content != null && content.contains("Your OTP")) {
//                    // For example, OTP is a 6-digit number
//                    otp = extractOtp(content);  // You should implement a regex to extract the OTP from the email content
//                    break;
//                }
//            }
//
//            inbox.close(false);
//            store.close();
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return otp;
//    }
//
//    // Sample OTP extraction (you can adjust based on the format of the OTP in your email)
//    private String extractOtp(String emailContent) {
//        String otp = null;
//        // Regex for extracting 6-digit OTP
//        String regex = "\\b\\d{6}\\b";
//        Pattern pattern = Pattern.compile(regex);
//        Matcher matcher = pattern.matcher(emailContent);
//        if (matcher.find()) {
//            otp = matcher.group();
//        }
//        return otp;
//    }
}
