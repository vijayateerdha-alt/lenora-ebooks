package com.example

import com.example.data.catalog.CatalogSeeder
import com.example.data.catalog.MangaSeeder
import com.example.data.security.PasswordHasher
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun `password hasher generates unique salts and verifies correctly`() {
        val password = "MySecretPassword123"
        val salt = PasswordHasher.generateSalt()
        val hash = PasswordHasher.hashPassword(password, salt)

        // Verifies correct password
        assertTrue(PasswordHasher.verifyPassword(password, salt, hash))

        // Fails with wrong password
        assertFalse(PasswordHasher.verifyPassword("WrongPassword", salt, hash))

        // Different salt produces different hash
        val salt2 = PasswordHasher.generateSalt()
        val hash2 = PasswordHasher.hashPassword(password, salt2)
        assertNotEquals(salt, salt2)
        assertNotEquals(hash, hash2)
    }

    @Test
    fun `manga seeder generates expansive catalog of at least 1000 manga`() {
        val mangaList = MangaSeeder.generateExpansiveMangaCatalog()
        assertTrue("Manga catalog should contain at least 1000 items, found: ${mangaList.size}", mangaList.size >= 1000)

        // Verify properties of seeded manga
        val sampleManga = mangaList.first()
        assertTrue(sampleManga.title.isNotEmpty())
        assertTrue(sampleManga.genres.isNotEmpty())
        assertTrue(sampleManga.totalChapters > 0)
    }

    @Test
    fun `books catalog seeder generates at least 1000 books`() {
        val books = CatalogSeeder.getExpansivePublicDomainCatalog()
        assertTrue("Books catalog should contain at least 1000 items, found: ${books.size}", books.size >= 1000)
    }
}
