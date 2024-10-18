## Issue Description:


One of our customer environment experienced a gradual increase in disk space utilization. Upon investigation, it was found that a specific error was being logged repeatedly into a file which had following log rotation config - 
maxBytes=10MB and backupCount=3
```
RotatingFileHandler('api_process_internal.log', maxBytes=10 * 1024 * 1024, backupCount=3)
```

The error occurred due to a special character present (question mark ?) in the password (o89GH!i9Ud5?NL_) which was used for sql_alchemy_conn url in Airflow.
We need this enviroment variable to let airflow worker know here to persist the metadata -
AIRFLOW__DATABASE__SQL_ALCHEMY_CONN.
```
env {"AIRFLOW__DATABASE__SQL_ALCHEMY_CONN": f"postgresql+psycopg2://{db_user}:{db_password}@"
f"{db_host}:{db_port}/{self.airflow_db}",
"AIRFLOW__CORE__SQL_ALCHEMY_CONN": f"postgresql+psycopg2://{db_user}:{db_password}@"
f"{db_host}:{db_port}/{self.airflow_db}",
"AIRFLOW__CELERY__RESULT_BACKEND": f"db+postgresql://{db_user}:{db_password}@"
f"{db_host}:{db_port}/{self.airflow_db}",
}
```

Use of special character in pwd caused an error while establishing a connection to the database, error was -
```
Traceback (most recent call last):[0m
  File "/usr/local/lib/python3.10/dist-packages/celery/worker/worker.py", line 203, in start[0m
    self.blueprint.start(self)[0m
  File "/usr/local/lib/python3.10/dist-packages/celery/bootsteps.py", line 112, in start[0m
    self.on_start()[0m
  File "/usr/local/lib/python3.10/dist-packages/celery/apps/worker.py", line 136, in on_start[0m
    self.emit_banner()[0m
  File "/usr/local/lib/python3.10/dist-packages/celery/apps/worker.py", line 170, in emit_banner[0m
    ' \n', self.startup_info(artlines=not use_image))),[0m
  File "/usr/local/lib/python3.10/dist-packages/celery/apps/worker.py", line 232, in startup_info[0m
    results=self.app.backend.as_uri(),[0m
  File "/usr/local/lib/python3.10/dist-packages/celery/backends/base.py", line 151, in as_uri[0m
    url = maybe_sanitize_url(self.url or '')[0m
  File "/usr/local/lib/python3.10/dist-packages/kombu/utils/url.py", line 112, in maybe_sanitize_url[0m
    return sanitize_url(url, mask)[0m
  File "/usr/local/lib/python3.10/dist-packages/kombu/utils/url.py", line 105, in sanitize_url[0m
    return as_url(*_parse_url(url), sanitize=True, mask=mask)[0m
  File "/usr/local/lib/python3.10/dist-packages/kombu/utils/url.py", line 70, in url_to_parts[0m
    parts.port,[0m
  File "/usr/lib/python3.10/urllib/parse.py", line 185, in port[0m
    raise ValueError(f"Port could not be cast to integer value as {port!r}")[0m
ValueError: Port could not be cast to integer value as 'o89GH!i9Ud5'[0m
[2024-10-15 13:29:15 +0000] [56571] [INFO] Handling signal: term[0m
[2024-10-15 13:29:15 +0000] [56572] [INFO] Worker exiting (pid: 56572)[0m
[2024-10-15 13:29:15 +0000] [56573] [INFO] Worker exiting (pid: 56573)[0m
[2024-10-15 13:29:15 +0000] [56571] [INFO] Shutting down: Master[0m
```

Furthermore, the log rotation mechanism was not functioning correctly. Even after log files were rotated and deleted, the files were still kept open, resulting in a large number of open file descriptors. This issue was caused by a bug in the code responsible for log rotation.
Customer saw more than thousands of opened files (`lsof -n | grep deleted`) which showed that even log files were deleted but were in open state which was consuming space in disk.


## Troubleshooting Steps:
### Initial Investigation:
- Noted and Examined disk space utilization per hour and identified a gradual increase. 
- Analyzed log files and observed the repetitive occurrence of a specific error related to the database connection.
- Used command lsof -n | grep deleted to see list of opened deleted file.
- Verified the software / some some libary's version to make sure customer is using right version of sw.

### Identifying the Root Cause:
- Tried to reproduce at our end.
- Reviewed the code to find possible cause of the error. 
- During simulating the issue, discovered that the special character in the password was causing the connection error. 
- Verified that the log rotation mechanism was not closing the file descriptors after rotation and deletion.

### Resolution Approach:
- To resolve the database connection error, the code was modified to URL-encode the password using urllib.parse.quote_plus() before using it in the AIRFLOW__DATABASE__SQL_ALCHEMY_CONN environment variable.
```
import urllib.parse
encoded_db_password = url.parse.quote_plus(db_password)

env = {"AIRFLOW__DATABASE__SQL_ALCHEMY_CONN": f"postgresql+psycopg2://{db_user}:{encoded_db_password}@"
                                       f"{db_host}:{db_port}/{self.airflow_db}",
"AIRFLOW__CORE__SQL_ALCHEMY_CONN": f"postgresql+psycopg2://{db_user}:{encoded_db_password}@"
                                   f"{db_host}:{db_port}/{self.airflow_db}",
"AIRFLOW__CELERY__RESULT_BACKEND": f"db+postgresql://{db_user}:{encoded_db_password}@"
                                   f"{db_host}:{db_port}/{self.airflow_db}",
}                                   

```

- The log rotation code was updated to ensure proper closure of file descriptors after log rotation and deletion.

### Implementation and Testing:
- Created new version of the docker image, Deployed the image to the customer environment.
- Conducted thorough testing to validate the changes. Checked the now error is gone even though password had special character.
And for 1 day we Monitored disk space utilization and confirmed that the gradual increase issue was resolved.

- Verified that log rotation was functioning correctly, with no accumulation of open file descriptors.
  - For this we increased the log file manually to force log rotation for more than 3 times (as backupCount was 3)
  - and Checked when log rotation is deleting the older file to make room for next rotated file, the file is deleted without having opened forever in the system

