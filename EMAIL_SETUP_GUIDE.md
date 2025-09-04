# Email Notification Setup Guide

This guide will help you set up email notifications for your Naukri profile automation to receive execution results directly in your personal email.

## 📧 **Gmail Setup for Sending Emails**

### Step 1: Enable 2-Factor Authentication
1. Go to [Google Account Security](https://myaccount.google.com/security)
2. Enable 2-Step Verification if not already enabled

### Step 2: Generate App Password
1. Go to [App passwords](https://myaccount.google.com/apppasswords)
2. Select "Mail" as the app
3. Select "Other" as the device and enter "Naukri Automation"
4. Click "Generate"
5. **Save this 16-character password** - you'll need it for configuration

## 🔧 **Configuration Setup**

### For GitHub Actions:

Add these secrets in your GitHub repository (Settings → Secrets and variables → Actions):

```
NAUKRI_USERNAME: santhoshkumar18012002@gmail.com
NAUKRI_PASSWORD: saN!thosh@027
SENDER_EMAIL: your-gmail@gmail.com
SENDER_APP_PASSWORD: your-16-digit-app-password
RECIPIENT_EMAIL: santhoshkumar18012002@gmail.com
RESUME_URL: (optional - direct link to resume)
```

### For Jenkins:

Add these credentials in Jenkins (Manage Jenkins → Credentials):

```
naukri-username: santhoshkumar18012002@gmail.com
naukri-password: saN!thosh@027
sender-email: your-gmail@gmail.com
sender-app-password: your-16-digit-app-password
recipient-email: santhoshkumar18012002@gmail.com
```

### For Local Testing:

Set environment variables:

**Windows (PowerShell):**
```powershell
$env:NAUKRI_USERNAME="santhoshkumar18012002@gmail.com"
$env:NAUKRI_PASSWORD="saN!thosh@027"
$env:SENDER_EMAIL="your-gmail@gmail.com"
$env:SENDER_APP_PASSWORD="your-16-digit-app-password"
$env:RECIPIENT_EMAIL="santhoshkumar18012002@gmail.com"
```

**Windows (Command Prompt):**
```cmd
set NAUKRI_USERNAME=santhoshkumar18012002@gmail.com
set NAUKRI_PASSWORD=saN!thosh@027
set SENDER_EMAIL=your-gmail@gmail.com
set SENDER_APP_PASSWORD=your-16-digit-app-password
set RECIPIENT_EMAIL=santhoshkumar18012002@gmail.com
```

## 📧 **Email Templates**

You will receive different emails based on execution status:

### ✅ **Success Email**
- **Subject**: "✅ Naukri Profile Updated Successfully"
- **Content**: 
  - Location updates made (Chennai added/removed)
  - Resume upload status
  - Profile activity confirmation
  - Benefits achieved

### ❌ **Failure Email**
- **Subject**: "❌ Naukri Profile Update Failed"
- **Content**:
  - Error details
  - Possible causes
  - Troubleshooting steps
  - Links to execution logs

### ⚠️ **Partial Success Email**
- **Subject**: "⚠️ Naukri Profile Update - Partial Success"
- **Content**:
  - What succeeded vs what failed
  - Warnings encountered
  - Recommended actions

## 🛠️ **Troubleshooting Email Issues**

### Common Problems:

1. **"Email credentials not found"**
   - Verify environment variables are set correctly
   - Check GitHub secrets or Jenkins credentials

2. **"Authentication failed"**
   - Ensure 2FA is enabled on Gmail
   - Verify app password is correct (16 digits, no spaces)
   - Use app password, not regular Gmail password

3. **"Failed to send email"**
   - Check internet connectivity
   - Verify Gmail SMTP settings
   - Ensure sender email is correct

4. **Not receiving emails**
   - Check spam/junk folder
   - Verify recipient email address
   - Check Gmail's sent folder to confirm emails are being sent

### Debug Steps:

1. **Test email functionality locally:**
   ```bash
   mvn clean test -Dtest=DailyUpdateTestRunner -Dcucumber.filter.tags="@profileUpdate"
   ```

2. **Check console output for email logs:**
   - Look for "Email notification sent successfully"
   - Check for any error messages

3. **Verify credentials:**
   - Test with a simple email client
   - Confirm app password works

## 📋 **Email Content Example**

### Success Email Content:
```
✅ Naukri Profile Update - SUCCESS

Date & Time: 2025-09-04T07:30:00
Status: SUCCESS

Execution Summary:
✅ Location Update: Added Chennai to preferred locations
✅ Career profile changes saved successfully
✅ Resume Upload: Successfully uploaded - SanthoshKumarK.pdf
✅ Overall Status: Profile update completed successfully
✅ Profile Activity: Your profile now shows recent activity
✅ Recruiter Visibility: Increased visibility to potential employers

Next Steps:
• Your Naukri profile has been successfully updated
• Profile will show 'Updated Today' status
• Increased visibility to recruiters
```

## 🔒 **Security Best Practices**

1. **Never commit credentials to code**
2. **Use environment variables or secrets management**
3. **Regularly rotate app passwords**
4. **Monitor email logs for suspicious activity**
5. **Use dedicated email account for automation if possible**

## 📅 **Email Schedule**

- **Daily Success Emails**: Every day at 7:30 AM IST when automation succeeds
- **Failure Alerts**: Immediately when automation fails
- **No Spam**: Only one email per execution

## 🎯 **Benefits of Email Notifications**

1. **Immediate Feedback**: Know instantly if your profile was updated
2. **Peace of Mind**: Confirmation that automation is working
3. **Quick Troubleshooting**: Immediate alerts if something goes wrong
4. **Audit Trail**: Record of all profile update activities
5. **Mobile Friendly**: Read notifications on your phone anywhere

---

**Need Help?** Check the execution logs in GitHub Actions or Jenkins for detailed error information.
