### Note on Testing Notifications

Because the Firebase Admin Key is not shared (for security reasons), the Notification feature will not work out-of-the-box on your machine.

If you configure a new Firebase project and provide your own Admin Key via environment variables, you must also replace the app/google-services.json file with your own project's file and rebuild the Android app. Otherwise, you will get a SenderIdMismatch error due to Project ID mismatch.
