# Naukri Profile Daily Update Automation

This project automates daily profile updates on Naukri.com to keep your profile active and visible to recruiters.

## 🚀 Features

- **Daily Automated Updates**: Runs every day at 7:30 AM IST
- **Location Updates**: Randomly adds/removes Chennai and Bengaluru from preferred locations
- **Resume Upload**: Uploads your latest resume
- **Email Notifications**: Detailed execution reports sent to your personal email
- **Headless Execution**: Runs without browser UI in GitHub Actions
- **Manual Trigger**: Can be triggered manually when needed

## 🔧 Setup Instructions

### 1. GitHub Repository Setup

1. Push this code to your GitHub repository
2. Go to your repository settings → Secrets and variables → Actions
3. Add the following secrets:

```
NAUKRI_USERNAME: santhoshkumar18012002@gmail.com
NAUKRI_PASSWORD: ********
SENDER_EMAIL: your-gmail-account@gmail.com
SENDER_APP_PASSWORD: your-gmail-app-password
RECIPIENT_EMAIL: santhoshkumar18012002@gmail.com
RESUME_URL: https://your-resume-url.com/resume.pdf (optional)
```

**📧 Email Setup**: Follow the detailed [Email Setup Guide](EMAIL_SETUP_GUIDE.md) to configure Gmail for sending notifications.

### 2. Resume Setup Options

**Option A: Upload to GitHub (Recommended)**
1. Create a `src/test/resources/files/` folder in your repo
2. Upload your resume as `resume.pdf`
3. Commit and push

**Option B: External URL**
1. Upload your resume to Google Drive, Dropbox, or any cloud storage
2. Get a direct download link
3. Add it as `RESUME_URL` secret in GitHub

### 3. Local Testing

To test locally before deploying:

```bash
# Set environment variables
set NAUKRI_USERNAME=your_email@gmail.com
set NAUKRI_PASSWORD=your_password

# Run the specific test
mvn clean test -Dtest=DailyUpdateTestRunner
```

### 4. GitHub Actions Workflow

The workflow is configured to:
- Run daily at 7:30 AM IST (2:00 AM UTC)
- Use Ubuntu with Chrome headless browser
- Cache Maven dependencies for faster execution
- Upload test results as artifacts

## 📁 Project Structure

```
src/test/java/
├── features/
│   ├── dailyProfileUpdate.feature     # New feature for daily updates
│   ├── login.feature                  # Existing login tests
│   └── profile.feature               # Existing profile tests
├── stepDefinitions/
│   ├── DailyProfileUpdateStepDef.java # New step definitions
│   ├── LoginStepDef.java             # Existing login steps
│   └── ProfileStepDef.java           # Existing profile steps
├── testRunner/
│   ├── DailyUpdateTestRunner.java     # New test runner for automation
│   └── TestRunner.java               # Existing test runner
├── pageObjects/
├── factory/
└── hooks/

.github/workflows/
└── naukri-profile-update.yml         # GitHub Actions workflow
```

## 🔍 What the Automation Does

1. **Login**: Securely logs into your Naukri account
2. **Navigate to Profile**: Goes to your profile page
3. **Update Career Profile**: 
   - Opens career profile section
   - Randomly adds/removes Chennai from preferred locations
   - Saves the changes
4. **Upload Resume**: Updates your resume with the latest version
5. **Verify**: Confirms profile shows "Updated Today"
6. **Email Notification**: Sends detailed execution report to your email with:
   - Location update status (Chennai added/removed)
   - Resume upload confirmation
   - Overall success/failure status
   - Next steps and recommendations

## � **Email Notifications**

You'll receive detailed email reports for every execution:

### ✅ **Success Email**
- Subject: "✅ Naukri Profile Updated Successfully"
- Location update details (Chennai added/removed)
- Resume upload confirmation  
- Profile activity status
- Benefits achieved

### ❌ **Failure Email**  
- Subject: "❌ Naukri Profile Update Failed"
- Error details and possible causes
- Troubleshooting steps
- Links to execution logs

### 📧 **Email Setup Required**
Follow the [Email Setup Guide](EMAIL_SETUP_GUIDE.md) to configure:
1. Gmail app password generation
2. GitHub secrets configuration
3. Email template customization

## �📊 Monitoring

- Check GitHub Actions tab for execution logs
- Failed runs will be highlighted in red
- Test artifacts are saved for 7 days
- Manual trigger available for immediate testing

## ⚙️ Configuration

Edit `src/test/resources/config.properties`:

```properties
# For GitHub Actions (headless)
execution_env=local
headless=true
browser=chrome

# For local testing
execution_env=local
headless=false
browser=chrome

# Application URL
appURL=https://www.naukri.com

# Resume configuration
resume_path=src/test/resources/files/resume.pdf

# Email configuration  
recipient.email=santhoshkumar18012002@gmail.com
```

## 🔒 Security

- Credentials are stored as GitHub secrets (encrypted)
- No sensitive data is logged or exposed
- Headless execution prevents browser windows

## 🚨 Important Notes

1. **Rate Limiting**: Don't run too frequently to avoid account restrictions
2. **Account Safety**: Monitor for any unusual account activity
3. **Resume Updates**: Keep your resume file updated in the repository
4. **Location Logic**: Currently toggles Chennai; modify logic for other cities if needed

## 🛠️ Troubleshooting

### Common Issues:

1. **Login Failed**: Check username/password in GitHub secrets
2. **Resume Not Found**: Verify resume path and file exists
3. **Element Not Found**: Naukri might have changed their UI - update selectors
4. **Workflow Not Running**: Check cron syntax and GitHub Actions permissions

### Debug Steps:

1. Run locally first: `mvn clean test -Dtest=DailyUpdateTestRunner`
2. Check GitHub Actions logs for detailed error messages
3. Verify all secrets are properly set
4. Test with manual workflow trigger first

## 📞 Support

For issues or improvements:
1. Check GitHub Actions logs
2. Review test artifacts
3. Update selectors if Naukri UI changes
4. Modify timing/frequency as needed

---

**Disclaimer**: Use responsibly and in accordance with Naukri's terms of service. This automation is for legitimate profile maintenance purposes only.
