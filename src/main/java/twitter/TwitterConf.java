package twitter;

import twitter4j.conf.ConfigurationBuilder;

public class TwitterConf {

    private ConfigurationBuilder configurationBuilder;
    private String consumerKey;
    private String accessToken;
    private String accessTokenSecrtet;

    public TwitterConf(String consumerKey, String accessToken, String accessTokenSecret) {
        this.consumerKey = consumerKey;
        this.accessToken = accessToken;
        this.accessTokenSecrtet = accessTokenSecret;
        this.configurationBuilder = new ConfigurationBuilder();
        this.configurationBuilder
                .setOAuthConsumerKey(consumerKey)
                .setOAuthAccessToken(accessToken)
                .setOAuthAccessTokenSecret(accessTokenSecret);
    }

    public ConfigurationBuilder getConfigurationBuilder() {
        return configurationBuilder;
    }

    public String getQueryString() {
        return "";
    }
}
