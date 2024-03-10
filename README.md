1) Config-Service Application
     i) install docker desktop
     ii) docker build -t configservice .
     ii) docker run -d -p 8080:8080 -p 8500:8500 --name config-service configservice
