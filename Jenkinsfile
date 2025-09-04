pipeline {
    agent any
    
    triggers {
        // Run daily at 7:30 AM IST (2:00 AM UTC)
        cron('0 2 * * *')
    }
    
    environment {
        NAUKRI_USERNAME = credentials('naukri-username')
        NAUKRI_PASSWORD = credentials('naukri-password')
        SENDER_EMAIL = credentials('sender-email')
        SENDER_APP_PASSWORD = credentials('sender-app-password')
        RECIPIENT_EMAIL = credentials('recipient-email')
        MAVEN_OPTS = '-Dmaven.test.failure.ignore=true'
    }
    
    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }
        
        stage('Setup') {
            steps {
                script {
                    // Update config for headless execution
                    sh '''
                        sed -i 's/headless=false/headless=true/' src/test/resources/config.properties
                        echo "Updated config for headless execution"
                    '''
                }
            }
        }
        
        stage('Download Resume') {
            steps {
                script {
                    // Download resume if URL is provided
                    if (env.RESUME_URL) {
                        sh '''
                            mkdir -p src/test/resources/files
                            curl -L "$RESUME_URL" -o src/test/resources/files/resume.pdf
                            echo "Resume downloaded successfully"
                        '''
                    } else {
                        echo "No resume URL provided, using existing file"
                    }
                }
            }
        }
        
        stage('Install Chrome') {
            steps {
                sh '''
                    # Install Chrome if not already installed
                    if ! command -v google-chrome &> /dev/null; then
                        wget -q -O - https://dl.google.com/linux/linux_signing_key.pub | apt-key add -
                        echo "deb [arch=amd64] http://dl.google.com/linux/chrome/deb/ stable main" > /etc/apt/sources.list.d/google-chrome.list
                        apt-get update
                        apt-get install -y google-chrome-stable
                    fi
                '''
            }
        }
        
        stage('Run Profile Update') {
            steps {
                script {
                    try {
                        sh '''
                            mvn clean test -Dtest=DailyUpdateTestRunner -Dcucumber.filter.tags="@profileUpdate"
                        '''
                    } catch (Exception e) {
                        currentBuild.result = 'UNSTABLE'
                        echo "Test execution completed with issues: ${e.getMessage()}"
                    }
                }
            }
        }
        
        stage('Generate Reports') {
            steps {
                script {
                    // Publish test results
                    publishHTML([
                        allowMissing: false,
                        alwaysLinkToLastBuild: true,
                        keepAll: true,
                        reportDir: 'target/cucumber-reports',
                        reportFiles: 'index.html',
                        reportName: 'Cucumber Report'
                    ])
                    
                    // Archive test results
                    archiveArtifacts artifacts: 'target/surefire-reports/**/*', allowEmptyArchive: true
                    archiveArtifacts artifacts: 'target/cucumber-reports/**/*', allowEmptyArchive: true
                }
            }
        }
    }
    
    post {
        always {
            // Clean workspace
            cleanWs()
        }
        
        success {
            echo "Naukri profile updated successfully!"
            emailext (
                subject: "✅ Naukri Profile Updated Successfully - Build #${BUILD_NUMBER}",
                body: """
                <h2>✅ Naukri Profile Update - SUCCESS</h2>
                <p><strong>Date & Time:</strong> ${new Date()}</p>
                <p><strong>Build:</strong> #${BUILD_NUMBER}</p>
                <p><strong>Status:</strong> <span style="color: #28a745; font-weight: bold;">SUCCESS</span></p>
                
                <h3>What was updated:</h3>
                <ul>
                    <li>✅ Career profile location preferences updated</li>
                    <li>✅ Resume uploaded successfully</li>
                    <li>✅ Profile now shows "Updated Today" status</li>
                </ul>
                
                <h3>Benefits:</h3>
                <ul>
                    <li>Increased visibility to recruiters</li>
                    <li>Profile appears in recent searches</li>
                    <li>Shows active job seeking status</li>
                </ul>
                
                <p><a href="${BUILD_URL}">View detailed execution logs</a></p>
                <p><a href="${BUILD_URL}cucumber-reports/">View test reports</a></p>
                
                <p style="font-size: 12px; color: #666;">
                    This is an automated notification from your Naukri Profile Update automation.
                </p>
                """,
                mimeType: 'text/html',
                to: "${RECIPIENT_EMAIL}"
            )
        }
        
        failure {
            echo "Naukri profile update failed!"
            emailext (
                subject: "❌ Naukri Profile Update Failed - Build #${BUILD_NUMBER}",
                body: """
                <h2>❌ Naukri Profile Update - FAILED</h2>
                <p><strong>Date & Time:</strong> ${new Date()}</p>
                <p><strong>Build:</strong> #${BUILD_NUMBER}</p>
                <p><strong>Status:</strong> <span style="color: #dc3545; font-weight: bold;">FAILED</span></p>
                
                <h3>Possible Issues:</h3>
                <ul>
                    <li>❌ Login credentials might be incorrect</li>
                    <li>❌ Naukri website structure may have changed</li>
                    <li>❌ Network connectivity issues</li>
                    <li>❌ Resume file not found or accessible</li>
                </ul>
                
                <h3>Next Steps:</h3>
                <ul>
                    <li>Check the execution logs for detailed error information</li>
                    <li>Verify your Naukri credentials are correct</li>
                    <li>Ensure resume file is accessible</li>
                    <li>Consider manual profile update</li>
                </ul>
                
                <p><a href="${BUILD_URL}">View detailed error logs</a></p>
                <p><a href="${BUILD_URL}console">View console output</a></p>
                
                <p style="font-size: 12px; color: #666;">
                    This is an automated notification from your Naukri Profile Update automation.
                </p>
                """,
                mimeType: 'text/html',
                to: "${RECIPIENT_EMAIL}"
            )
        }
        
        unstable {
            echo "Naukri profile update completed with warnings!"
            emailext (
                subject: "⚠️ Naukri Profile Update - Partial Success - Build #${BUILD_NUMBER}",
                body: """
                <h2>⚠️ Naukri Profile Update - PARTIAL SUCCESS</h2>
                <p><strong>Date & Time:</strong> ${new Date()}</p>
                <p><strong>Build:</strong> #${BUILD_NUMBER}</p>
                <p><strong>Status:</strong> <span style="color: #ffc107; font-weight: bold;">PARTIAL SUCCESS</span></p>
                
                <h3>What happened:</h3>
                <ul>
                    <li>Some profile updates may have succeeded</li>
                    <li>Some steps encountered warnings or minor issues</li>
                    <li>Check detailed logs for specific information</li>
                </ul>
                
                <p><a href="${BUILD_URL}">View detailed execution logs</a></p>
                <p><a href="${BUILD_URL}cucumber-reports/">View test reports</a></p>
                
                <p style="font-size: 12px; color: #666;">
                    This is an automated notification from your Naukri Profile Update automation.
                </p>
                """,
                mimeType: 'text/html',
                to: "${RECIPIENT_EMAIL}"
            )
        }
    }
}
