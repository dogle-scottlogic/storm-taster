package database;

import redis.clients.jedis.*;

import java.util.Set;

public class JedisClient {

    private Jedis jedis;
    private final String MAP_NAME = "category";

    public JedisClient(String host, int port) {
        this.jedis = new Jedis(host, port);
    }

    // Add a tuple to the database
    public void setTuple(String key, String value) {
        try {
            jedis.set(key, value);
        } catch (Exception e) {
            printError(e.getMessage());
        }
    }

    // get a value from the database
    public String getValue(String key) {
        try {
            return jedis.get(key);
        } catch (Exception e) {
            printError(e.getMessage());
        }
        return null;
    }

    public void printAllCats() {
        try {
            System.out.println(jedis.hgetAll(MAP_NAME));
        } catch (Exception e) {
            printError(e.getMessage());
        }
    }

    public void addToMap(String category, Double pledge) {
        try {
            String oldPledge = jedis.hget(MAP_NAME, category);
            if (oldPledge != null) {
                pledge += Double.parseDouble(oldPledge);
            }
            jedis.hset(MAP_NAME, category, pledge.toString());
        } catch (Exception e) {
            printError(e.getMessage());
        }
    }

    public void  deleteAll() {
        try{
            jedis.flushAll();
        }catch (Exception e) {
            printError(e.getMessage());
        }
    }

    // print all the values in the database
    public void printDatabaseValues() {
        Set<String> keys = jedis.keys("*");
        for (String key : keys) {
            System.out.println(key + " : " + jedis.get(key));
        }
    }

    // handle errors
    public void printError(String error) {
        System.out.println(error);
    }
}
