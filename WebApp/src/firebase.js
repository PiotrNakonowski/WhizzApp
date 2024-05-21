import { initializeApp } from "firebase/app";
import { getAuth } from "firebase/auth";
import { getFirestore } from "firebase/firestore";


const firebaseConfig = {
  apiKey: process.env.REACT_APP_FIREBASE_KEY,
  authDomain: "myapp-e50c4.firebaseapp.com",
  databaseURL: "https://myapp-e50c4-default-rtdb.europe-west1.firebasedatabase.app",
  projectId: "myapp-e50c4",
  storageBucket: "myapp-e50c4.appspot.com",
  messagingSenderId: "179971525300",
  appId: "1:179971525300:web:e7f6a640822c01618e65e1"
};

const app = initializeApp(firebaseConfig);
export const db = getFirestore(app);
export const auth = getAuth();