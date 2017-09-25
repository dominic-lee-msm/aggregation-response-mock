package com.msm.aggregation.intercept;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.bson.types.ObjectId;
import org.jongo.RawResultHandler;
import org.jongo.marshall.jackson.oid.MongoId;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
public class SpringController {

    private MongoDbConnector mongoDbConnector;

    public SpringController(final MongoDbConnector mongoDbConnector) {
        this.mongoDbConnector = mongoDbConnector;
    }

    @RequestMapping("/")
    public ModelAndView defaultMapping(){
        return list();
    }

    @RequestMapping("/list")
    public ModelAndView list(){
        final List<Map<String, Object>> entries = ImmutableList.copyOf(mongoDbConnector.getCollection("configuration").find("{}").map(new RawResultHandler<>()).iterator()).stream().map(dbo -> (Map<String, Object>) dbo.toMap()).collect(Collectors.toList());
        final List<MockDisplayItem> displayItems = new ArrayList<>();
        for(Map<String, Object> entry : entries) {
            displayItems.add(new MockDisplayItem(
                    (ObjectId)entry.get("_id"),
                    (String)entry.get("name"),
                    (String)entry.get("target"),
                    entry.get("enabled").equals("Y"),
                    (String)entry.get("response")
            ));
        }
        return new ModelAndView("list-view", ImmutableMap.of("mockListItems", displayItems));
    }

    @RequestMapping("/manage/{id}")
    public ModelAndView manage(@PathVariable final String id){
        final List<Map<String, Object>> entries = ImmutableList.copyOf(mongoDbConnector.getCollection("configuration").find("{}").map(new RawResultHandler<>()).iterator()).stream().map(dbo -> (Map<String, Object>) dbo.toMap()).collect(Collectors.toList());
        Optional<MockDisplayItem> optItem = Optional.empty();
        for(Map<String, Object> entry : entries) {
            if(id.equals(entry.get("_id").toString())) {
                optItem = Optional.of(new MockDisplayItem(
                        (ObjectId) entry.get("_id"),
                        (String)entry.get("name"),
                        (String)entry.get("target"),
                        entry.get("enabled").equals("Y"),
                        (String)entry.get("response")
                ));
            }
        }
        final Map<String, MockDisplayItem> model = optItem.map(mock -> ImmutableMap.of("mock", mock)).orElse(ImmutableMap.of());
        return new ModelAndView("manage-view", model);
    }

    @RequestMapping("/manage")
    public ModelAndView manage(){
        final MockDisplayItem mock = new MockDisplayItem(ObjectId.get(), "", "", true, "");
        return new ModelAndView("manage-view", ImmutableMap.of("mock", mock));
    }

    @PostMapping("/save")
    public ModelAndView save(@ModelAttribute MockDisplayItem mock){
        final MockPersistenceItem toSave = new MockPersistenceItem(mock.getId(), mock.getName(), mock.getTarget(), mock.enabled ? "Y" : "N", mock.getResponse());
        mongoDbConnector.getCollection("configuration").save(toSave);
        refreshCaches();
        return list();
    }

    @RequestMapping("/delete/{id}")
    public ModelAndView delete(@PathVariable String id){
        mongoDbConnector.getCollection("configuration").remove(new ObjectId(id));
        refreshCaches();
        return list();
    }

    private void refreshCaches() {
        final String uriString = "http://localhost:" + Boot.PROXY_PORT_NUMBER + "/refresh-configurations";
        new RestTemplate().getForEntity(URI.create(uriString), Object.class);
    }

    private static final class MockPersistenceItem {

        @MongoId
        private final ObjectId id;
        private final String name;
        private final String target;
        private final String enabled;
        private final String response;

        public MockPersistenceItem(@MongoId final ObjectId id, final String name, final String target, final String enabled, final String response) {
            this.id = id;
            this.name = name;
            this.target = target;
            this.enabled = enabled;
            this.response = response;
        }

        public ObjectId getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getTarget() {
            return target;
        }

        public String getEnabled() {
            return enabled;
        }

        public String getResponse() {
            return response;
        }
    }

    private static final class MockDisplayItem {

        @MongoId
        private ObjectId id;
        private String name;
        private String target;
        private boolean enabled;
        private String response;

        public MockDisplayItem(@MongoId final ObjectId id, final String name, final String target, final boolean enabled, final String response) {
            this.id = id;
            this.name = name;
            this.target = target;
            this.enabled = enabled;
            this.response = response;
        }

        public MockDisplayItem(){}

        public ObjectId getId() {
            return id;
        }

        public void setId(ObjectId id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public String getTarget() {
            return target;
        }

        public boolean isEnabled() {
            return enabled;
        }

        public String getResponse() {
            return response;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setTarget(String target) {
            this.target = target;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public void setResponse(String response) {
            this.response = response;
        }
    }

}
