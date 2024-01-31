package com.example.authserver;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.MapDeserializer;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.node.MissingNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.common.util.OAuth2Utils;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.TokenRequest;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * ClassName:OAuth2AuthenticationSerializer
 * Package:com.example.authserver
 * Description:
 *
 * @Date:2024/1/23 17:13
 * @Author:qs@1.com
 */
public class OAuth2AuthenticationDeserializer extends StdDeserializer<OAuth2Authentication> {

    public OAuth2AuthenticationDeserializer() {
        super(OAuth2Authentication.class);
    }

    @Override
    public OAuth2Authentication deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        OAuth2Authentication oAuth2Authentication = doDeserialize(jp);
        return oAuth2Authentication;
    }

    private OAuth2Authentication doDeserialize(JsonParser jp) throws IOException {
        Set<String> authorities = null;
        Object details = null;
        boolean authenticated = false;
        Authentication userAuthentication = null;
        boolean clientOnly = false;
        String principal = null;
        String credentials = null;
        OAuth2Request oauth2Request = null;
        String name = null;

        ObjectMapper objectMapper = (ObjectMapper) jp.getCodec();
        ObjectNode root = objectMapper.readTree(jp);
        Iterator<Map.Entry<String, JsonNode>> elementsIterator = root.fields();
        while (elementsIterator.hasNext()) {
            Map.Entry<String, JsonNode> element = elementsIterator.next();
            String fieldname = element.getKey();
            JsonNode jsonNode = element.getValue();

            if ("authorities".equals(fieldname)) {
                authorities = jsonNode.findValuesAsText("authorities").stream().filter(StringUtils::hasText).collect(Collectors.toSet());
            } else if ("details".equals(fieldname)) {
                /**
                 * 参考了一下：
                 * @see UsernamePasswordAuthenticationTokenDeserializer#deserialize(JsonParser, DeserializationContext)
                 */
                JsonNode detailsNode = readJsonNode(jsonNode, "details");
                if (detailsNode.isNull() || detailsNode.isMissingNode()) {
                    details = null;
                } else {
                    details = objectMapper.readValue(detailsNode.toString(), new TypeReference<Object>() {});
                }
            } else if ("authenticated".equals(fieldname)) {
                authenticated = jsonNode.booleanValue();
            } else if ("userAuthentication".equals(fieldname)) {
                /**
                 * 使用 TypeReference<Authentication> 能够解决一些报错问题
                 * 实际效果还有待实际使用验证，再去进行改进
                 */
                userAuthentication = objectMapper.readValue(jsonNode.traverse(), new TypeReference<Authentication>() {});
            } else if ("clientOnly".equals(fieldname)) {
                clientOnly = jsonNode.booleanValue();
            } else if ("principal".equals(fieldname)) {
                principal = jsonNode.textValue();
            } else if ("oauth2Request".equals(fieldname)) {
                oauth2Request = parseOAuth2Request(jsonNode);
            } else if ("credentials".equals(fieldname)) {
                credentials = jsonNode.textValue();
            } else if ("name".equals(fieldname)) {
                name = jsonNode.textValue();
            }
        }

        OAuth2Authentication oAuth2Authentication = new OAuth2Authentication(oauth2Request, userAuthentication);
        oAuth2Authentication.setAuthenticated(authenticated);
        oAuth2Authentication.setDetails(details);
        return oAuth2Authentication;
    }


    private JsonNode readJsonNode(JsonNode jsonNode, String field) {
        return jsonNode.has(field) ? jsonNode.get(field) : MissingNode.getInstance();
    }

    @Override
    public Object deserializeWithType(JsonParser p, DeserializationContext ctxt, TypeDeserializer typeDeserializer) throws IOException {
        return deserialize(p, ctxt);
    }

    private OAuth2Request parseOAuth2Request(JsonNode jsonNode) throws IOException {
        JsonNode requestParametersJsonNode = jsonNode.findValue("requestParameters");

        /**
         * Map<String, String> requestParameters =
         *                 objectMapper.readValue(requestParametersJsonNode.traverse(), new TypeReference<Map<String, String>>() {});
         * 不能使用上面的方式   Map -> {@link com.fasterxml.jackson.databind.deser.std.MapDeserializer}
         * 因为 {@link org.springframework.security.jackson2.CoreJackson2Module} 的原因走的是
         * {@link MapDeserializer#deserializeWithType(JsonParser, DeserializationContext, TypeDeserializer)} 会报错 type id missing
         */
        Map<String, String> requestParameters = new HashMap<>();
        Iterator<Map.Entry<String, JsonNode>> iterator = requestParametersJsonNode.fields();
        while (iterator.hasNext()) {
            Map.Entry<String, JsonNode> entry = iterator.next();
            requestParameters.put(entry.getKey(), entry.getValue().textValue());
        }

        String clientId = jsonNode.get("clientId").textValue();
        String grantType = jsonNode.get("grantType").textValue();
        String redirectUri = jsonNode.get("redirectUri").textValue();
        boolean approved = jsonNode.get("approved").booleanValue();
        Set<String> responseTypes = jsonNode.findValuesAsText("responseTypes").stream().filter(StringUtils::hasText).collect(Collectors.toSet());


        Set<String> scope = jsonNode.findValuesAsText("scope").stream().filter(StringUtils::hasText).collect(Collectors.toSet());

        Set<String> authorities = jsonNode.findValuesAsText("authorities").stream().filter(StringUtils::hasText).collect(Collectors.toSet());
        Set<GrantedAuthority> grantedAuthorities = new HashSet<>(0);
        if (authorities != null && !authorities.isEmpty()) {
            authorities.forEach(s -> grantedAuthorities.add(new SimpleGrantedAuthority(s)));
        }

        Set<String> resourceIds = jsonNode.findValuesAsText("resourceIds").stream().filter(StringUtils::hasText).collect(Collectors.toSet());

        Map<String, Serializable> extensions = new HashMap<>();
        Iterator<Map.Entry<String, JsonNode>> extensionsIterator = jsonNode.get("extensions").fields();
        while (extensionsIterator.hasNext()) {
            Map.Entry<String, JsonNode> entry = extensionsIterator.next();
            extensions.put(entry.getKey(), entry.getValue().textValue());
        }

        OAuth2Request request = new OAuth2Request(requestParameters, clientId,
                grantedAuthorities, approved, scope, resourceIds, redirectUri, responseTypes, extensions);
        TokenRequest tokenRequest = new TokenRequest(requestParameters, clientId, scope, grantType);
        request = request.refresh(tokenRequest);
        return request;
    }
}
