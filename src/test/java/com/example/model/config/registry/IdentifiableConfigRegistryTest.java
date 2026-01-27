package com.example.model.config.registry;

import com.example.model.config.IdentifiableConfig;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

class IdentifiableConfigRegistryTest {

    private static class TestConfig extends IdentifiableConfig {
        private String name;

        @Override
        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (!(o instanceof TestConfig))
                return false;
            TestConfig that = (TestConfig) o;
            return id.equals(that.id) && Objects.equals(name, that.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id, name);
        }
    }

    private static class TestRegistry extends IdentifiableConfigRegistry<TestConfig> {
    }

    private static TestRegistry registry;
    private static ObjectMapper mapper;

    @BeforeAll
    static void setUp() {
        registry = new TestRegistry();
        mapper = new ObjectMapper();
    }

    @AfterEach
    void tearDownEach() {
        registry.unload();
    }

    @Test
    void testLoadValidConfigs() {
        String jsonPath = "/test-configs.json"; // exists in src/test/resources
        registry.load(mapper, jsonPath, new TypeReference<List<TestConfig>>() {
        });
        assertTrue(registry.isLoaded());
        assertEquals(2, registry.size());
        assertEquals("A", registry.get("1").getName());
        assertEquals("B", registry.get("2").getName());
    }

    @Test
    void testLoadResourceNotFound() {
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            registry.load(mapper, "/nonexistent.json", new TypeReference<List<TestConfig>>() {
            });
        });
        assertNotNull(exception.getCause()); // The underlying cause
        assertTrue(exception.getCause() instanceof IllegalArgumentException);
    }

    @Test
    void testLoadDuplicateIds() {
        String jsonPath = "/test-configs-duplicate.json"; // duplicate IDs in JSON
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            registry.load(mapper, jsonPath, new TypeReference<List<TestConfig>>() {
            });
        });
        // Now you can inspect the exception
        assertNotNull(exception.getCause()); // The underlying cause
        assertTrue(exception.getCause() instanceof IllegalStateException);
    }

    @Test
    void testLoadMalformedJson() {
        String jsonPath = "/test-configs-malformed.json"; // invalid JSON
        assertThrows(RuntimeException.class,
                () -> registry.load(mapper, jsonPath, new TypeReference<List<TestConfig>>() {
                }));
    }

    @Test
    void testUnloadClearsEntries() {
        String jsonPath = "/test-configs.json"; // exists in src/test/resources
        registry.load(mapper, jsonPath, new TypeReference<List<TestConfig>>() {
        });
        assertTrue(registry.isLoaded());
        registry.unload();
        assertFalse(registry.isLoaded());
        assertEquals(0, registry.size());
    }

    @Test
    void testGetNonExistentIdReturnsNull() {
        String jsonPath = "/test-configs.json"; // exists in src/test/resources
        registry.load(mapper, jsonPath, new TypeReference<List<TestConfig>>() {
        });
        assertNull(registry.get("999")); // ID 999 does not exist
    }

    @Test
    void testAllReturnsUnmodifiableCollection() {
        String jsonPath = "/test-configs.json"; // exists in src/test/resources
        registry.load(mapper, jsonPath, new TypeReference<List<TestConfig>>() {
        });
        var allConfigs = registry.all();
        assertEquals(2, allConfigs.size());
        assertThrows(UnsupportedOperationException.class, () -> {
            allConfigs.clear();
        });
    }

    @Test
    void testSizeReturnsCorrectCount() {
        String jsonPath = "/test-configs.json"; // exists in src/test/resources
        registry.load(mapper, jsonPath, new TypeReference<List<TestConfig>>() {
        });
        assertEquals(2, registry.size());
    }

    @Test
    void testGetReturnsCorrectConfig() {
        String jsonPath = "/test-configs.json"; // exists in src/test/resources
        registry.load(mapper, jsonPath, new TypeReference<List<TestConfig>>() {
        });
        assertNotNull(registry.get("1"));
        assertEquals("A", registry.get("1").getName());
    }

    @Test
    void testIsLoaded() {
        assertFalse(registry.isLoaded());
        String jsonPath = "/test-configs.json"; // exists in src/test/resources
        registry.load(mapper, jsonPath, new TypeReference<List<TestConfig>>() {
        });
        assertTrue(registry.isLoaded());
    }

    @Test
    public void testLoadTwiceThrows() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        String jsonPath = "/test-configs.json";
        registry.load(mapper, jsonPath, new TypeReference<List<TestConfig>>() {
        });
        assertThrows(IllegalStateException.class, () -> {
            registry.load(mapper, jsonPath, new TypeReference<List<TestConfig>>() {
            });
        });
    }


}
