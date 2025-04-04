SET SCHEMA VIDEOBASE;

CREATE VIEW V_USER_STATS AS
    SELECT U.USER_ID, U.USERNAME, V.VIDEO_COUNT,
           V.AVERAGE_TIME, S1.SUBSCRIPTIONS, S2.SUBSCRIBERS
    FROM USERS U
    LEFT JOIN (
        SELECT USER_ID, COUNT(VIDEO_ID) AS VIDEO_COUNT,
           AVG(LENGTH) AS AVERAGE_TIME
        FROM VIDEOS
        GROUP BY USER_ID
    ) V ON U.USER_ID = V.USER_ID
    LEFT JOIN (
        SELECT SUBSCRIBER_ID, COUNT(SUBSCRIBER_ID) AS SUBSCRIPTIONS
        FROM SUBSCRIBERS
        GROUP BY SUBSCRIBER_ID
    ) S1 ON U.USER_ID = S1.SUBSCRIBER_ID
    LEFT JOIN (
        SELECT SUBSCRIBED_TO_ID, COUNT(SUBSCRIBED_TO_ID) AS SUBSCRIBERS
        FROM SUBSCRIBERS
        GROUP BY SUBSCRIBED_TO_ID
    ) S2 ON U.USER_ID = S2.SUBSCRIBED_TO_ID;

CREATE VIEW V_CAMERA_USAGE AS
    SELECT * FROM
        (SELECT C.MANUFACTURER, C.MODEL,
               COUNT(V.VIDEO_ID) AS VIDEOS
            FROM CAMERAS C
            LEFT JOIN VIDEOS V ON C.MANUFACTURER = V.CAM_MANUFACTURER
                                      AND C.MODEL = V.CAM_MODEL
            GROUP BY C.MODEL,C.MANUFACTURER
            ORDER BY VIDEOS DESC);

CREATE VIEW V_VIDEOS AS
    SELECT V.VIDEO_ID, V.TITLE, V.LENGTH, V.USER_ID, C.MANUFACTURER, C.MODEL
    FROM VIDEOS V
    LEFT JOIN CAMERAS C
    ON C.MANUFACTURER = V.CAM_MANUFACTURER AND C.MODEL = V.CAM_MODEL;