package idv.tia201.g1.chat.utils;

import idv.tia201.g1.entity.ChatParticipant;
import idv.tia201.g1.utils.basic.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

import static idv.tia201.g1.utils.Constants.CACHE_CHAT_PARTICIPANT;
import static idv.tia201.g1.utils.Constants.LOCK_CHAT_PARTICIPANT;


@Slf4j
@Component
public class ChatCacheClient {
    private static final Long LOCK_TTL = 10L;
    private final StringRedisTemplate stringRedisTemplate;


    public ChatCacheClient(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    public void mapPut(String key, String hashKey, Object value, Long time, TimeUnit unit) {
        stringRedisTemplate.opsForHash().put(key, hashKey, JSONUtil.toJsonStr(value));
        stringRedisTemplate.expire(key, time, unit);
    }

    public void mapPutAll(String key, Map<String, String> valueMap, Long time, TimeUnit unit) {
        stringRedisTemplate.opsForHash().putAll(key, valueMap);
        stringRedisTemplate.expire(key, time, unit);
    }

    public ChatParticipant queryParticipantWithMutex(
            Long chatId,
            Long mappingUserId,
            Long time,
            TimeUnit unit,
            Function<Long[], ChatParticipant> dbFallback
    ) {
        String key = CACHE_CHAT_PARTICIPANT + chatId;
        String lockKey = LOCK_CHAT_PARTICIPANT + chatId;
        Long[] ids = {chatId, mappingUserId};

        try {
            while (true) {
                // 從Redis查詢緩存
                String json = (String) stringRedisTemplate.opsForHash().get(key, mappingUserId.toString());
                if (json != null && !json.trim().isEmpty()) {
                    // 資料存在於Redis中, 將結果直接返回
                    return JSONUtil.toBean(json, ChatParticipant.class);
                } else if ("".equals(json)) {
                    // 查到的東西是"", 表示目前SQL中沒有這筆資料
                    return null;
                }

                // 緩存重建: 獲取互斥鎖
                if (!tryLock(lockKey)) {
                    // 鎖定失敗: 休眠一段時間重試
                    Thread.sleep(50);
                    // 原本是使用遞迴  但覺得會可能造成棧溢出  改成while自旋 (感覺後面還可以增加重試上限避免死鎖)
                } else {
                    // 成功獲取互斥鎖: 二次確認是否有其他人已經重建完緩存
                    // 如果有其他人已經重建緩存 回傳重建後緩存並解鎖 (兩段幾乎一樣的程式碼看起來好煩躁)
                    json = (String) stringRedisTemplate.opsForHash().get(key, mappingUserId.toString());
                    if (json != null && !json.trim().isEmpty()) {
                        // 資料存在於Redis中  將結果直接返回
                        unlock(lockKey);  // 手動釋放鎖 (沒解10秒後鎖也會自動失效, 但還是要記得解鎖)
                        return JSONUtil.toBean(json, ChatParticipant.class);
                    } else if ("".equals(json)) {
                        // 查到的東西是"", 表示目前SQL中沒有這筆資料
                        unlock(lockKey);  // 手動釋放鎖 (沒解10秒後鎖也會自動失效, 但還是要記得解鎖)
                        return null;
                    }
                    break;
                }
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        try {
            // 鎖獲取成功 且二次確認數據不存在
            // 執行緩存重建: 去SQL查詢 並寫入Redis
            ChatParticipant participant = dbFallback.apply(ids);
            if (participant == null) {
                // 資料庫中不存在這筆資料 將空值寫入Redis 返回null
                mapPut(key, mappingUserId.toString(), "", time, unit);
                return null;
            }
            // 寫入Redis
            mapPut(key, mappingUserId.toString(), participant, time, unit);
            // 返回查詢結果
            return participant;
        } finally {
            // 釋放互斥鎖 (這裡才可以用finally釋放鎖)
            // 上方用finally釋放的話, 會變成一獲得鎖就直接釋放
            unlock(lockKey);
        }
    }

    public List<ChatParticipant> getAllParticipantsList(Long chatId, Long time, TimeUnit unit, Function<Long, List<ChatParticipant>> dbFallback) {
        String key = CACHE_CHAT_PARTICIPANT + chatId;
        String lockKey = LOCK_CHAT_PARTICIPANT + chatId;
        try {
            while (true) {
                // 從Redis查詢緩存
                Map<Object, Object> resultMap = stringRedisTemplate.opsForHash().entries(key);
                if (!resultMap.isEmpty()) {
                    // 資料存在於Redis中, 將結果直接返回
                    return convertToList(resultMap);
                }

                // 緩存重建: 獲取互斥鎖
                if (!tryLock(lockKey)) {
                    // 鎖定失敗: 休眠一段時間重試
                    Thread.sleep(50);
                    // 原本是使用遞迴  但覺得會可能造成棧溢出  改成while自旋 (感覺後面還可以增加重試上限避免死鎖)
                } else {
                    // 成功獲取互斥鎖: 二次確認是否有其他人已經重建完緩存
                    // 如果有其他人已經重建緩存 回傳重建後緩存並解鎖 (兩段幾乎一樣的程式碼看起來好煩躁)
                    resultMap = stringRedisTemplate.opsForHash().entries(key);
                    if (!resultMap.isEmpty()) {
                        // 資料存在於Redis中, 將結果直接返回
                        List<ChatParticipant> res = convertToList(resultMap);
                        unlock(lockKey);  // 手動釋放鎖 (沒解10秒後鎖也會自動失效, 但還是要記得解鎖)
                        return res;
                    }
                    break;
                }
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        try {
            // 鎖獲取成功 且二次確認數據不存在
            // 執行緩存重建: 去SQL查詢 並寫入Redis
            List<ChatParticipant> res = dbFallback.apply(chatId);
            // 寫入Redis
            Map<String, String> participantMap = res.stream()
                    .collect(Collectors.toMap(
                            chatParticipant -> chatParticipant.getMappingUserId().toString(),
                            JSONUtil::toJsonStr
                    ));
            this.mapPutAll(key, participantMap, time, unit);
            // 返回查詢結果
            return res;
        } finally {
            // 釋放互斥鎖 (這裡才可以用finally釋放鎖)
            // 上方用finally釋放的話, 會變成一獲得鎖就直接釋放
            unlock(lockKey);
        }
    }

    private List<ChatParticipant> convertToList(Map<Object, Object> resultMap) {
        List<ChatParticipant> res = new ArrayList<>(resultMap.size());
        for (Map.Entry<Object, Object> entry : resultMap.entrySet()) {
            String json = (String) entry.getValue();
            ChatParticipant bean = JSONUtil.toBean(json, ChatParticipant.class);
            res.add(bean);
        }
        return res;
    }

    private boolean tryLock(String key) {
        Boolean flag = stringRedisTemplate.opsForValue().setIfAbsent(key, "lock", LOCK_TTL, TimeUnit.SECONDS);
        return flag != null && flag;
    }

    private void unlock(String key) {
        stringRedisTemplate.delete(key);
    }
}

