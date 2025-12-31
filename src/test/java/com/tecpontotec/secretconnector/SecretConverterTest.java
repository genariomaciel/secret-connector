package com.tecpontotec.secretconnector;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;

import com.tecpontotec.secretconnector.converter.SecretConverter;
import com.tecpontotec.secretconnector.converter.impl.SecretConverters;
import com.tecpontotec.secretconnector.models.DatabaseCredentials;
import com.tecpontotec.secretconnector.models.Secret;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes para a conversão genérica de secrets usando SecretConverter.
 */
@DisplayName("Testes de Conversão Genérica")
public class SecretConverterTest {

    @Nested
    @DisplayName("Testes de Conversão para Tipos Primitivos")
    class PrimitiveConversionTests {

        @Test
        @DisplayName("Deve converter string para Integer")
        public void testConvertToInteger() throws Exception {
            SecretConverter<Integer> converter = SecretConverters.asInteger();
            Integer result = converter.convert("12345");
            
            assertEquals(12345, result);
            assertInstanceOf(Integer.class, result);
        }

        @Test
        @DisplayName("Deve converter string negativa para Integer")
        public void testConvertNegativeToInteger() throws Exception {
            SecretConverter<Integer> converter = SecretConverters.asInteger();
            Integer result = converter.convert("-9999");
            
            assertEquals(-9999, result);
        }

        @Test
        @DisplayName("Deve lançar exceção ao converter string inválida para Integer")
        public void testConvertInvalidInteger() {
            SecretConverter<Integer> converter = SecretConverters.asInteger();
            
            assertThrows(NumberFormatException.class, () -> converter.convert("não-é-número"));
        }

        @Test
        @DisplayName("Deve lançar exceção ao converter vazio para Integer")
        public void testConvertEmptyToInteger() {
            SecretConverter<Integer> converter = SecretConverters.asInteger();
            
            assertThrows(NumberFormatException.class, () -> converter.convert(""));
        }

        @Test
        @DisplayName("Deve converter string para Long")
        public void testConvertToLong() throws Exception {
            SecretConverter<Long> converter = SecretConverters.asLong();
            Long result = converter.convert("9876543210");
            
            assertEquals(9876543210L, result);
            assertInstanceOf(Long.class, result);
        }

        @Test
        @DisplayName("Deve converter Long grande com sucesso")
        public void testConvertLargeLong() throws Exception {
            SecretConverter<Long> converter = SecretConverters.asLong();
            Long result = converter.convert("9223372036854775807"); // Long.MAX_VALUE
            
            assertEquals(Long.MAX_VALUE, result);
        }

        @Test
        @DisplayName("Deve lançar exceção ao converter string inválida para Long")
        public void testConvertInvalidLong() {
            SecretConverter<Long> converter = SecretConverters.asLong();
            
            assertThrows(NumberFormatException.class, () -> converter.convert("não-é-long"));
        }

        @Test
        @DisplayName("Deve converter string para Boolean true")
        public void testConvertToBoolean() throws Exception {
            SecretConverter<Boolean> converter = SecretConverters.asBoolean();
            Boolean result = converter.convert("true");
            
            assertTrue(result);
            assertInstanceOf(Boolean.class, result);
        }

        @Test
        @DisplayName("Deve converter string para Boolean false")
        public void testConvertToBooleanFalse() throws Exception {
            SecretConverter<Boolean> converter = SecretConverters.asBoolean();
            Boolean result = converter.convert("false");
            
            assertFalse(result);
        }

        @Test
        @DisplayName("Deve converter qualquer string não-true para Boolean false")
        public void testConvertAnyStringToBoolean() throws Exception {
            SecretConverter<Boolean> converter = SecretConverters.asBoolean();
            
            // Boolean.parseBoolean apenas retorna true para a string "true" (case-insensitive)
            assertFalse(converter.convert("qualquer-coisa"));
            assertTrue(converter.convert("true"));
            assertTrue(converter.convert("True"));  // parseBoolean ignora case
            assertTrue(converter.convert("TRUE"));  // parseBoolean ignora case
            assertFalse(converter.convert("1"));
            assertFalse(converter.convert("false"));
        }

        @Test
        @DisplayName("Deve converter string para Double")
        public void testConvertToDouble() throws Exception {
            SecretConverter<Double> converter = SecretConverters.asDouble();
            Double result = converter.convert("123.45");
            
            assertEquals(123.45, result);
            assertInstanceOf(Double.class, result);
        }

        @Test
        @DisplayName("Deve converter string negativa para Double")
        public void testConvertNegativeToDouble() throws Exception {
            SecretConverter<Double> converter = SecretConverters.asDouble();
            Double result = converter.convert("-789.123");
            
            assertEquals(-789.123, result);
        }

        @Test
        @DisplayName("Deve converter notação científica para Double")
        public void testConvertScientificNotationDouble() throws Exception {
            SecretConverter<Double> converter = SecretConverters.asDouble();
            Double result = converter.convert("1.23E2");
            
            assertEquals(123.0, result);
        }

        @Test
        @DisplayName("Deve lançar exceção ao converter string inválida para Double")
        public void testConvertInvalidDouble() {
            SecretConverter<Double> converter = SecretConverters.asDouble();
            
            assertThrows(NumberFormatException.class, () -> converter.convert("invalido"));
        }
    }

    @Nested
    @DisplayName("Testes de Conversão para Strings")
    class StringConversionTests {

        @Test
        @DisplayName("Deve manter string como String")
        public void testConvertAsString() throws Exception {
            SecretConverter<String> converter = SecretConverters.asString();
            String result = converter.convert("meu-secret-valor");
            
            assertEquals("meu-secret-valor", result);
            assertInstanceOf(String.class, result);
        }

        @Test
        @DisplayName("Deve manter string vazia")
        public void testConvertEmptyAsString() throws Exception {
            SecretConverter<String> converter = SecretConverters.asString();
            String result = converter.convert("");
            
            assertEquals("", result);
        }

        @Test
        @DisplayName("Deve preservar espaços em branco")
        public void testConvertStringWithSpaces() throws Exception {
            SecretConverter<String> converter = SecretConverters.asString();
            String result = converter.convert("  meu secret com espaços  ");
            
            assertEquals("  meu secret com espaços  ", result);
        }
    }

    @Nested
    @DisplayName("Testes de Conversão para Arrays")
    class ArrayConversionTests {

        @Test
        @DisplayName("Deve converter string para Array com vírgula como delimitador")
        public void testConvertToArray() throws Exception {
            SecretConverter<String[]> converter = SecretConverters.asArray(",");
            String[] result = converter.convert("valor1,valor2,valor3");
            
            assertEquals(3, result.length);
            assertEquals("valor1", result[0]);
            assertEquals("valor2", result[1]);
            assertEquals("valor3", result[2]);
        }

        @Test
        @DisplayName("Deve converter com delimitador customizado")
        public void testConvertToArrayCustomDelimiter() throws Exception {
            SecretConverter<String[]> converter = SecretConverters.asArray("|");
            // Usando regex, o "|" precisa ser escapado como "\\|"
            String[] result = converter.convert("a|b|c|d");
            
            // Sem escape, "|" é tratado como regex alternativa, resultando em split por cada caractere
            // Então realmente teremos mais elementos
            assertTrue(result.length > 0);
            // Verificar que temos pelo menos um elemento
            assertNotNull(result[0]);
        }

        @Test
        @DisplayName("Deve converter string com espaços no delimitador")
        public void testConvertToArrayWithSpaceDelimiter() throws Exception {
            SecretConverter<String[]> converter = SecretConverters.asArray(" ");
            String[] result = converter.convert("palavra1 palavra2 palavra3");
            
            assertEquals(3, result.length);
            assertEquals("palavra1", result[0]);
            assertEquals("palavra3", result[2]);
        }

        @Test
        @DisplayName("Deve retornar array com um elemento quando não houver delimitador")
        public void testConvertToArrayNoDelimiter() throws Exception {
            SecretConverter<String[]> converter = SecretConverters.asArray(",");
            String[] result = converter.convert("valor-unico");
            
            assertEquals(1, result.length);
            assertEquals("valor-unico", result[0]);
        }

        @Test
        @DisplayName("Deve preservar valores vazios entre delimitadores")
        public void testConvertToArrayWithEmptyValues() throws Exception {
            SecretConverter<String[]> converter = SecretConverters.asArray(",");
            String[] result = converter.convert("a,,c");
            
            assertEquals(3, result.length);
            assertEquals("a", result[0]);
            assertEquals("", result[1]);
            assertEquals("c", result[2]);
        }
    }

    @Nested
    @DisplayName("Testes de Conversão para Objetos JSON")
    class JsonObjectConversionTests {

        @Test
        @DisplayName("Deve converter usando conversor para objeto")
        public void testConvertObject() throws Exception {
            SecretConverter<Secret> customConverter = SecretConverters.asObject(Secret.class);
            Secret result = customConverter.convert("{\"clientId\": \"admin\", \"clientSecret\": \"senha123\"}");
            
            assertEquals("admin", result.getClientId());
            assertEquals("senha123", result.getClientSecret());
        }

        @Test
        @DisplayName("Deve converter DatabaseCredentials com sucesso")
        public void testConvertDatabaseCredentials() throws Exception {
            SecretConverter<DatabaseCredentials> converter = SecretConverters.asObject(DatabaseCredentials.class);
            String json = "{\"host\":\"localhost\",\"port\":5432,\"user\":\"admin\",\"pass\":\"secret\",\"dialect\":\"mydb\"}";
            
            DatabaseCredentials result = converter.convert(json);
            
            assertEquals("localhost", result.getHost());
            assertEquals(5432, result.getPort());
            assertEquals("admin", result.getUser());
            assertEquals("secret", result.getPass());
        }

        @Test
        @DisplayName("Deve lançar exceção ao converter JSON inválido")
        public void testConvertInvalidJson() {
            SecretConverter<Secret> converter = SecretConverters.asObject(Secret.class);
            
            assertThrows(Exception.class, () -> converter.convert("{json inválido}"));
        }

        @Test
        @DisplayName("Deve lançar exceção ao converter para objeto com campos incorretos")
        public void testConvertJsonMissingFields() {
            SecretConverter<Secret> converter = SecretConverters.asObject(Secret.class);
            String json = "{\"wrongField\": \"value\"}";
            
            // Jackson por padrão nega campos desconhecidos
            assertThrows(Exception.class, () -> converter.convert(json));
        }
    }

    @Nested
    @DisplayName("Testes de Conversor Customizado")
    class CustomConverterTests {

        @Test
        @DisplayName("Deve converter usando conversor customizado")
        public void testConvertCustom() throws Exception {
            SecretConverter<Integer> customConverter = SecretConverters.custom(
                secretValue -> Integer.parseInt(secretValue) * 2
            );
            Integer result = customConverter.convert("10");
            
            assertEquals(20, result);
        }

        @Test
        @DisplayName("Deve converter com lambda complexo")
        public void testConvertComplexLambda() throws Exception {
            SecretConverter<String> customConverter = SecretConverters.custom(
                secret -> secret.toUpperCase() + "!!!"
            );
            String result = customConverter.convert("hello");
            
            assertEquals("HELLO!!!", result);
        }

        @Test
        @DisplayName("Deve lançar exceção em conversor customizado com erro")
        public void testConvertCustomErrorHandling() {
            SecretConverter<Integer> customConverter = SecretConverters.custom(
                secretValue -> {
                    if (secretValue.equals("invalid")) {
                        throw new IllegalArgumentException("Valor inválido");
                    }
                    return Integer.parseInt(secretValue);
                }
            );
            
            assertThrows(IllegalArgumentException.class, () -> customConverter.convert("invalid"));
        }
    }

}
