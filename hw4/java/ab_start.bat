for /l %%n in () do (
  ab\ab.exe -n 50 -c 10 http://10.75.40.19:9088/users 
  timeout /t 1
)