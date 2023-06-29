/*
package com.example.biblioteca_nazionale

import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.firestore.Firestore
import com.google.cloud.firestore.FirestoreOptions
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.io.File
import java.io.FileInputStream


class FirestoreTest {
    private val COLLECTION_NAME = "TEST COLLECTION"
    private lateinit var firestore: Firestore

    @Before
    fun setUp() {
        // Configura le credenziali per l'autenticazione
        val file = File("C:/Users/lucab/OneDrive/Desktop/Ingegneria/TERZO_ANNO/SECONDO_SEMESTRE/Programmazione Mobile/Progetto/Biblioteca_Nazionale/app/google-services.json")
        val absolutePath = file.absolutePath
        val credentials = GoogleCredentials.fromStream(FileInputStream(absolutePath))
        val options = FirestoreOptions.newBuilder()
            .setCredentials(credentials)
            .build()

        // Ottieni un'istanza di Firestore
        firestore = options.service
    }

    @After
    fun tearDown() {
        // Cancella tutti i documenti creati durante i test
        val collectionRef = firestore.collection(COLLECTION_NAME)
        val documents = collectionRef.listDocuments()
        for (document in documents) {
            document.delete()
        }
    }

    @Test
    fun testWriteAndReadData() {
        val testData = mapOf(
            "name" to "John Doe",
            "age" to 30,
            "city" to "New York"
        )

        val documentRef = firestore.collection(COLLECTION_NAME).document("testDocument")
        documentRef.set(testData).get()

        val snapshot = documentRef.get().get()
        val retrievedData = snapshot.data!!

        assertNotNull(retrievedData)
        assertEquals("John Doe", retrievedData["name"])
        assertEquals(30L, retrievedData["age"])
        assertEquals("New York", retrievedData["city"])
    }
}

*/