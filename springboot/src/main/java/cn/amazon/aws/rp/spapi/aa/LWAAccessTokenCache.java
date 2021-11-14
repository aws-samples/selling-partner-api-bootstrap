package cn.amazon.aws.rp.spapi.aa;

public interface LWAAccessTokenCache {
  String get(Object key);
  void put(Object key, String accessToken, long tokenTTLInSeconds);
}
