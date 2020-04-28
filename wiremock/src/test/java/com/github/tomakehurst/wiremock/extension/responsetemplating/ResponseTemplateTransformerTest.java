/*
 * Copyright (C) 2011 Thomas Akehurst
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.tomakehurst.wiremock.extension.responsetemplating;

import com.github.jknack.handlebars.EscapingStrategy;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Helper;
import com.github.jknack.handlebars.Options;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.common.ClasspathFileSource;
import com.github.tomakehurst.wiremock.extension.Parameters;
import com.github.tomakehurst.wiremock.http.Request;
import com.github.tomakehurst.wiremock.http.ResponseDefinition;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.matching.MockRequest.mockRequest;
import static com.github.tomakehurst.wiremock.testsupport.NoFileSource.noFileSource;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

public class ResponseTemplateTransformerTest {

    private ResponseTemplateTransformer transformer;

    @Before
    public void setup() {
        transformer = new ResponseTemplateTransformer(true);
    }

    @Test
    public void queryParameters() {
        ResponseDefinition transformedResponseDef = transform(mockRequest()
                .url("/things?multi_param=one&multi_param=two&single-param=1234"),
            aResponse().withBody(
                "Multi 1: {{request.query.multi_param.[0]}}, Multi 2: {{request.query.multi_param.[1]}}, Single 1: {{request.query.single-param}}"
            )
        );

        assertThat(transformedResponseDef.getBody(), is(
            "Multi 1: one, Multi 2: two, Single 1: 1234"
        ));
    }

    @Test
    public void showsNothingWhenNoQueryParamsPresent() {
        ResponseDefinition transformedResponseDef = transform(mockRequest()
                .url("/things"),
            aResponse().withBody(
                "{{request.query.multi_param.[0]}}"
            )
        );

        assertThat(transformedResponseDef.getBody(), is(""));
    }

    @Test
    public void requestHeaders() {
        ResponseDefinition transformedResponseDef = transform(mockRequest()
                .url("/things")
                .header("X-Request-Id", "req-id-1234")
                .header("123$%$^&__why_o_why", "foundit"),
            aResponse().withBody(
                "Request ID: {{request.headers.X-Request-Id}}, Awkward named header: {{request.headers.[123$%$^&__why_o_why]}}"
            )
        );

        assertThat(transformedResponseDef.getBody(), is(
            "Request ID: req-id-1234, Awkward named header: foundit"
        ));
    }

    @Test
    public void cookies() {
        ResponseDefinition transformedResponseDef = transform(mockRequest()
                .url("/things")
                .cookie("session", "session-1234")
                .cookie(")((**#$@#", "foundit"),
            aResponse().withBody(
                "session: {{request.cookies.session}}, Awkward named cookie: {{request.cookies.[)((**#$@#]}}"
            )
        );

        assertThat(transformedResponseDef.getBody(), is(
            "session: session-1234, Awkward named cookie: foundit"
        ));
    }

    @Test
    public void multiValueCookies() {
        ResponseDefinition transformedResponseDef = transform(mockRequest()
                .url("/things")
                .cookie("multi", "one", "two"),
            aResponse().withBody(
                "{{request.cookies.multi}}, {{request.cookies.multi.[0]}}, {{request.cookies.multi.[1]}}"
            )
        );

        assertThat(transformedResponseDef.getBody(), is(
            "one, one, two"
        ));
    }

    @Test
    public void urlPath() {
        ResponseDefinition transformedResponseDef = transform(mockRequest()
                .url("/the/entire/path"),
            aResponse().withBody(
                "Path: {{request.path}}"
            )
        );

        assertThat(transformedResponseDef.getBody(), is(
            "Path: /the/entire/path"
        ));
    }

    @Test
    public void urlPathNodes() {
        ResponseDefinition transformedResponseDef = transform(mockRequest()
                .url("/the/entire/path"),
            aResponse().withBody(
                "First: {{request.path.[0]}}, Last: {{request.path.[2]}}"
            )
        );

        assertThat(transformedResponseDef.getBody(), is(
            "First: the, Last: path"
        ));
    }

    @Test
    public void urlPathNodesForRootPath() {
        ResponseDefinition transformedResponseDef = transform(mockRequest()
                .url("/"),
            aResponse().withBody(
                "{{request.path.[0]}}"
            )
        );

        assertThat(transformedResponseDef.getBody(), is(""));
    }

    @Test
    public void fullUrl() {
        ResponseDefinition transformedResponseDef = transform(mockRequest()
                .url("/the/entire/path?query1=one&query2=two"),
            aResponse().withBody(
                "URL: {{{request.url}}}"
            )
        );

        assertThat(transformedResponseDef.getBody(), is(
            "URL: /the/entire/path?query1=one&query2=two"
        ));
    }

    @Test
    public void templatizeBodyFile() {
        ResponseDefinition transformedResponseDef = transformFromResponseFile(mockRequest()
                .url("/the/entire/path?name=Ram"),
            aResponse().withBodyFile(
                "/greet-{{request.query.name}}.txt"
            )
        );

        assertThat(transformedResponseDef.getBody(), is("Hello Ram"));
    }

    @Test
    public void requestBody() {
        ResponseDefinition transformedResponseDef = transform(mockRequest()
                .url("/things")
                .body("All of the body content"),
            aResponse().withBody(
                "Body: {{{request.body}}}"
            )
        );

        assertThat(transformedResponseDef.getBody(), is(
            "Body: All of the body content"
        ));
    }

    @Test
    public void singleValueTemplatedResponseHeaders() {
        ResponseDefinition transformedResponseDef = transform(mockRequest()
                .url("/things")
                .header("X-Correlation-Id", "12345"),
            aResponse().withHeader("X-Correlation-Id", "{{request.headers.X-Correlation-Id}}")
        );

        assertThat(transformedResponseDef
                .getHeaders()
                .getHeader("X-Correlation-Id")
                .firstValue(),
            is("12345")
        );
    }

    @Test
    public void multiValueTemplatedResponseHeaders() {
        ResponseDefinition transformedResponseDef = transform(mockRequest()
                .url("/things")
                .header("X-Correlation-Id-1", "12345")
                .header("X-Correlation-Id-2", "56789"),
            aResponse().withHeader("X-Correlation-Id",
                "{{request.headers.X-Correlation-Id-1}}",
                "{{request.headers.X-Correlation-Id-2}}")
        );

        List<String> headerValues = transformedResponseDef
            .getHeaders()
            .getHeader("X-Correlation-Id")
            .values();

        assertThat(headerValues.get(0), is("12345"));
        assertThat(headerValues.get(1), is("56789"));
    }

    @Test
    public void stringHelper() {
        ResponseDefinition transformedResponseDef = transform(mockRequest()
                .url("/things")
                .body("some text"),
            aResponse().withBody(
                "{{{ capitalize request.body }}}"
            )
        );

        assertThat(transformedResponseDef.getBody(), is(
            "Some Text"
        ));
    }

    @Test
    public void conditionalHelper() {
        ResponseDefinition transformedResponseDef = transform(mockRequest()
                        .url("/things")
                        .header("X-Thing", "1"),
                aResponse().withBody(
                        "{{#eq request.headers.X-Thing.[0] '1'}}ONE{{else}}MANY{{/eq}}"
                )
        );

        assertThat(transformedResponseDef.getBody(), is("ONE"));
    }

    @Test
    public void customHelper() {
        Helper<String> helper = new Helper<String>() {
            @Override
            public Object apply(String context, Options options) throws IOException {
                return context.length();
            }
        };

        transformer = ResponseTemplateTransformer.builder()
                .global(false)
                .helper("string-length", helper)
                .build();

        ResponseDefinition transformedResponseDef = transform(mockRequest()
                .url("/things")
                .body("fiver"),
            aResponse().withBody(
                "{{{ string-length request.body }}}"
            )
        );

        assertThat(transformedResponseDef.getBody(), is("5"));
    }
    
    @Test
    public void areConditionalHelpersLoaded() {

        ResponseDefinition transformedResponseDef = transform(mockRequest()
                .url("/things")
                .body("fiver"),
            aResponse().withBody(
                "{{{eq 5 5 yes='y' no='n'}}}"
            )
        );

        assertThat(transformedResponseDef.getBody(), is("y"));
    }
    
    
    

    @Test
    public void proxyBaseUrlWithAdditionalRequestHeader() {
        ResponseDefinition transformedResponseDef = transform(mockRequest()
                .url("/things")
                .header("X-WM-Uri", "http://localhost:8000"),
            aResponse().proxiedFrom("{{request.headers.X-WM-Uri}}")
                .withAdditionalRequestHeader("X-Origin-Url", "{{request.url}}")
        );

        assertThat(transformedResponseDef.getProxyBaseUrl(), is(
            "http://localhost:8000"
        ));
        assertNotNull(transformedResponseDef.getAdditionalProxyRequestHeaders());
        assertThat(transformedResponseDef
                        .getAdditionalProxyRequestHeaders()
                        .getHeader("X-Origin-Url")
                        .firstValue(),
                is("/things")
        );
    }

    @Test
    public void escapingIsTheDefault() {
        final ResponseDefinition responseDefinition = this.transformer.transform(
                mockRequest()
                        .url("/json").
                        body("{\"a\": {\"test\": \"look at my 'single quotes'\"}}"),
                aResponse()
                        .withBody("{\"test\": \"{{jsonPath request.body '$.a.test'}}\"}").build(),
                noFileSource(),
                Parameters.empty());

        assertThat(responseDefinition.getBody(), is("{\"test\": \"look at my &#x27;single quotes&#x27;\"}"));
    }

    @Test
    public void escapingCanBeDisabled() {
        Handlebars handlebars = new Handlebars().with(EscapingStrategy.NOOP);
        ResponseTemplateTransformer transformerWithEscapingDisabled = ResponseTemplateTransformer.builder()
                .global(true)
                .handlebars(handlebars)
                .build();
        final ResponseDefinition responseDefinition = transformerWithEscapingDisabled.transform(
                mockRequest()
                        .url("/json").
                        body("{\"a\": {\"test\": \"look at my 'single quotes'\"}}"),
                aResponse()
                        .withBody("{\"test\": \"{{jsonPath request.body '$.a.test'}}\"}").build(),
                noFileSource(),
                Parameters.empty());

        assertThat(responseDefinition.getBody(), is("{\"test\": \"look at my 'single quotes'\"}"));
    }

    @Test
    public void transformerParametersAreAppliedToTemplate() throws Exception {
        ResponseDefinition responseDefinition = transformer.transform(
                mockRequest()
                        .url("/json").
                        body("{\"a\": {\"test\": \"look at my 'single quotes'\"}}"),
                aResponse()
                        .withBody("{\"test\": \"{{parameters.variable}}\"}").build(),
                noFileSource(),
                Parameters.one("variable", "some.value")
        );

        assertThat(responseDefinition.getBody(), is("{\"test\": \"some.value\"}"));
    }

    @Test
    public void unknownTransformerParametersAreNotCausingIssues() throws Exception {
        ResponseDefinition responseDefinition = transformer.transform(
                mockRequest()
                        .url("/json").
                        body("{\"a\": {\"test\": \"look at my 'single quotes'\"}}"),
                aResponse()
                        .withBody("{\"test1\": \"{{parameters.variable}}\", \"test2\": \"{{parameters.unknown}}\"}").build(),
                noFileSource(),
                Parameters.one("variable", "some.value")
        );

        assertThat(responseDefinition.getBody(), is("{\"test1\": \"some.value\", \"test2\": \"\"}"));
    }

    @Test
    public void requestLineScheme() {
        ResponseDefinition transformedResponseDef = transform(mockRequest()
                .scheme("https")
                .host("my.domain.io")
                .port(8080)
                .url("/the/entire/path?query1=one&query2=two"),
            aResponse().withBody(
                "scheme: {{{request.requestLine.scheme}}}"
            )
        );

        assertThat(transformedResponseDef.getBody(), is(
            "scheme: https"
        ));
    }

    @Test
    public void requestLineHost() {
        ResponseDefinition transformedResponseDef = transform(mockRequest()
                .scheme("https")
                .host("my.domain.io")
                .port(8080)
                .url("/the/entire/path?query1=one&query2=two"),
            aResponse().withBody(
                "host: {{{request.requestLine.host}}}"
            )
        );

        assertThat(transformedResponseDef.getBody(), is(
            "host: my.domain.io"
        ));
    }

    @Test
    public void requestLinePort() {
        ResponseDefinition transformedResponseDef = transform(mockRequest()
                .scheme("https")
                .host("my.domain.io")
                .port(8080)
                .url("/the/entire/path?query1=one&query2=two"),
            aResponse().withBody(
                "port: {{{request.requestLine.port}}}"
            )
        );

        assertThat(transformedResponseDef.getBody(), is(
            "port: 8080"
        ));
    }

    @Test
    public void requestLinePath() {
        ResponseDefinition transformedResponseDef = transform(mockRequest()
                .scheme("https")
                .host("my.domain.io")
                .port(8080)
                .url("/the/entire/path?query1=one&query2=two"),
            aResponse().withBody(
                "path: {{{request.requestLine.path}}}"
            )
        );

        assertThat(transformedResponseDef.getBody(), is(
            "path: /the/entire/path?query1=one&query2=two"
        ));
    }

    @Test
    public void requestLineBaseUrlNonStandardPort() {
        ResponseDefinition transformedResponseDef = transform(mockRequest()
                .scheme("https")
                .host("my.domain.io")
                .port(8080)
                .url("/the/entire/path?query1=one&query2=two"),
            aResponse().withBody(
                "baseUrl: {{{request.requestLine.baseUrl}}}"
            )
        );

        assertThat(transformedResponseDef.getBody(), is(
            "baseUrl: https://my.domain.io:8080"
        ));
    }

    @Test
    public void requestLineBaseUrlHttp() {
        ResponseDefinition transformedResponseDef = transform(mockRequest()
                .scheme("http")
                .host("my.domain.io")
                .port(80)
                .url("/the/entire/path?query1=one&query2=two"),
            aResponse().withBody(
                "baseUrl: {{{request.requestLine.baseUrl}}}"
            )
        );

        assertThat(transformedResponseDef.getBody(), is(
            "baseUrl: http://my.domain.io"
        ));
    }

    @Test
    public void requestLineBaseUrlHttps() {
        ResponseDefinition transformedResponseDef = transform(mockRequest()
                .scheme("https")
                .host("my.domain.io")
                .port(443)
                .url("/the/entire/path?query1=one&query2=two"),
            aResponse().withBody(
                "baseUrl: {{{request.requestLine.baseUrl}}}"
            )
        );

        assertThat(transformedResponseDef.getBody(), is(
            "baseUrl: https://my.domain.io"
        ));
    }

    @Test
    public void requestLinePathSegment() {
        ResponseDefinition transformedResponseDef = transform(mockRequest()
                .scheme("https")
                .host("my.domain.io")
                .port(8080)
                .url("/the/entire/path?query1=one&query2=two"),
            aResponse().withBody(
                "path segments: {{{request.requestLine.pathSegments}}}"
            )
        );

        assertThat(transformedResponseDef.getBody(), is(
            "path segments: /the/entire/path"
        ));
    }

    @Test
    public void requestLinePathSegment0() {
        ResponseDefinition transformedResponseDef = transform(mockRequest()
                .scheme("https")
                .host("my.domain.io")
                .port(8080)
                .url("/the/entire/path?query1=one&query2=two"),
            aResponse().withBody(
                "path segments 0: {{{request.requestLine.pathSegments.[0]}}}"
            )
        );

        assertThat(transformedResponseDef.getBody(), is(
            "path segments 0: the"
        ));
    }

    @Test
    public void requestLinequeryParameters() {
        ResponseDefinition transformedResponseDef = transform(mockRequest()
                .url("/things?multi_param=one&multi_param=two&single-param=1234"),
            aResponse().withBody(
                "Multi 1: {{request.requestLine.query.multi_param.[0]}}, Multi 2: {{request.requestLine.query.multi_param.[1]}}, Single 1: {{request.requestLine.query.single-param}}"
            )
        );

        assertThat(transformedResponseDef.getBody(), is(
            "Multi 1: one, Multi 2: two, Single 1: 1234"
        ));
    }

    @Test
    public void trimContent() {
        String body = transform("{{#trim}}\n" +
                "{\n" +
                "  \"data\": \"spaced out JSON\"\n" +
                "}\n" +
                "     {{/trim}}");

        assertThat(body, is("{\n" +
                "  \"data\": \"spaced out JSON\"\n" +
                "}"));
    }

    @Test
    public void trimValue() {
        String body = transform("{{trim '   stuff  '}}");
        assertThat(body, is("stuff"));
    }

    @Test
    public void base64EncodeContent() {
        String body = transform("{{#base64}}hello{{/base64}}");
        assertThat(body, is("aGVsbG8="));
    }

    @Test
    public void base64EncodeValue() {
        String body = transform("{{{base64 'hello'}}}");
        assertThat(body, is("aGVsbG8="));
    }

    @Test
    public void base64DecodeValue() {
        String body = transform("{{{base64 'aGVsbG8=' decode=true}}}");
        assertThat(body, is("hello"));
    }

    @Test
    public void urlEncodeValue() {
        String body = transform("{{{urlEncode 'one two'}}}");
        assertThat(body, is("one+two"));
    }

    @Test
    public void urlDecodeValue() {
        String body = transform("{{{urlEncode 'one+two' decode=true}}}");
        assertThat(body, is("one two"));
    }

    @Test
    public void extractFormValue() {
        String body = transform("{{{formData request.body 'form'}}}{{{form.item2}}}", "item1=one&item2=two%202&item3=three%203");
        assertThat(body, is("two%202"));
    }

    @Test
    public void extractFormMultiValue() {
        String body = transform(
                "{{{formData request.body 'form'}}}{{form.item.1}}", "item=1&item=two%202&item=3"
        );
        assertThat(body, is("two%202"));
    }

    @Test
    public void extractFormValueWithUrlDecoding() {
        String body = transform("{{{formData request.body 'form' urlDecode=true}}}{{{form.item2}}}", "item1=one&item2=two%202&item3=three%203");
        assertThat(body, is("two 2"));
    }

    @Test
    public void extractSingleRegexValue() {
        String body = transform("{{regexExtract request.body '[A-Z]+'}}", "abc-DEF-123");
        assertThat(body, is("DEF"));
    }

    @Test
    public void extractMultipleRegexValues() {
        String body = transform("{{regexExtract request.body '([a-z]+)-([A-Z]+)-([0-9]+)' 'parts'}}{{parts.0}},{{parts.1}},{{parts.2}}", "abc-DEF-123");
        assertThat(body, is("abc,DEF,123"));
    }

    @Test
    public void calculateStringSize() {
        String body = transform("{{size 'abcde'}}");
        assertThat(body, is("5"));
    }

    @Test
    public void calculateListSize() {
        String body = transform(
                mockRequest().url("/stuff?things=1&things=2&things=3&things=4"),
                ok("{{size request.query.things}}"))
                .getBody();

        assertThat(body, is("4"));
    }

    @Test
    public void calculateMapSize() {
        String body = transform(
                mockRequest().url("/stuff?one=1&two=2&three=3"),
                ok("{{size request.query}}"))
                .getBody();

        assertThat(body, is("3"));
    }

    @Test
    public void firstListElement() {
        String body = transform(
                mockRequest().url("/stuff?things=1&things=2&things=3&things=4"),
                ok("{{request.query.things.first}}"))
                .getBody();

        assertThat(body, is("1"));
    }

    @Test
    public void lastListElement() {
        String body = transform(
                mockRequest().url("/stuff?things=1&things=2&things=3&things=4"),
                ok("{{request.query.things.last}}"))
                .getBody();

        assertThat(body, is("4"));
    }

    @Test
    public void listElementOffsetFromEnd() {
        String body = transform(
                mockRequest().url("/stuff?things=1&things=2&things=3&things=4"),
                ok("{{request.query.things.[-2]}}"))
                .getBody();

        assertThat(body, is("2"));
    }

    @Test
    public void listElementOffsetFromEnd2() {
        String body = transform(
                mockRequest().url("/stuff?things=1&things=2&things=3&things=4"),
                ok("{{request.query.things.[-1]}}"))
                .getBody();

        assertThat(body, is("3"));
    }

    @Test
    public void correctlyRendersWhenContentExistsEitherSideOfTemplate() {
        String body = transform(
                mockRequest().url("/stuff?one=1&two=2"),
                ok("Start \n\n {{request.query.one}} middle {{{request.query.two}}} end\n"))
                .getBody();

        assertThat(body, is("Start \n\n 1 middle 2 end\n"));
    }

    @Test
    public void clearsTemplateCacheOnReset() {
        transform("{{now}}");
        assertThat(transformer.getCacheSize(), greaterThan(0L));

        transformer.afterStubsReset();

        assertThat(transformer.getCacheSize(), is(0L));
    }

    @Test
    public void clearsTemplateCacheWhenAnyStubRemovedReset() {
        transform("{{now}}");
        assertThat(transformer.getCacheSize(), greaterThan(0L));

        transformer.afterStubRemoved(get(anyUrl()).build());

        assertThat(transformer.getCacheSize(), is(0L));
    }

    @Test
    public void honoursCacheSizeLimit() {
        transformer = ResponseTemplateTransformer.builder()
                .maxCacheEntries(3L)
                .build();

        transform("{{now}} 1");
        transform("{{now}} 2");
        transform("{{now}} 3");
        transform("{{now}} 4");
        transform("{{now}} 5");

        assertThat(transformer.getCacheSize(), is(3L));
    }

    @Test
    public void honours0CacheSizeLimit() {
        transformer = ResponseTemplateTransformer.builder()
                .maxCacheEntries(0L)
                .build();

        transform("{{now}} 1");
        transform("{{now}} 2");
        transform("{{now}} 3");
        transform("{{now}} 4");
        transform("{{now}} 5");

        assertThat(transformer.getCacheSize(), is(0L));
    }

    private String transform(String responseBodyTemplate) {
        return transform(mockRequest(), aResponse().withBody(responseBodyTemplate)).getBody();
    }

    private String transform(String responseBodyTemplate, String requestBody) {
        return transform(mockRequest().body(requestBody), aResponse().withBody(responseBodyTemplate)).getBody();
    }

    private ResponseDefinition transform(Request request, ResponseDefinitionBuilder responseDefinitionBuilder) {
        return transformer.transform(
            request,
            responseDefinitionBuilder.build(),
            noFileSource(),
            Parameters.empty()
        );
    }

    private ResponseDefinition transformFromResponseFile(Request request, ResponseDefinitionBuilder responseDefinitionBuilder) {
        return transformer.transform(
            request,
            responseDefinitionBuilder.build(),
            new ClasspathFileSource(this.getClass().getClassLoader().getResource("templates").getPath()),
            Parameters.empty()
        );
    }
}
