const functions = require("firebase-functions");
const admin = require("firebase-admin");

const serviceAccount = require("./serviceAccountKey.json");

admin.initializeApp({
  credential: admin.credential.cert(serviceAccount),
  databaseURL: "https://myapp-e50c4-default-rtdb.europe-west1.firebasedatabase.app",
});

const db = admin.firestore();

exports.deleteUser = functions.https.onCall(async (data, context) => {
  const uid = data.uid;

  try {
    // Usuń użytkownika z Firebase Authentication
    await admin.auth().deleteUser(uid);

    // Usuń użytkownika z Firestore
    await db.collection("users").doc(uid).delete();

    return { success: true };
  } catch (error) {
    console.error("Error deleting user:", error);
    return { error: error.message };
  }
});
