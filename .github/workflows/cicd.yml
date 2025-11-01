name: CI/CD Pipeline

on:
  push:
    branches: [ "master" ]
  pull_request:
    branches: [ "master" ]

env:
  DOCKER_IMAGE: ${{ secrets.DOCKERHUB_USERNAME }}/clicknbuy

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
    - uses: actions/checkout@v3
    
    - name: Set up JDK 17
      uses: actions/setup-java@v3
      with:
        java-version: '17'
        distribution: 'temurin'
        cache: maven
    
    - name: Build with Maven
      run: mvn clean package -DskipTests
      
    - name: Run Tests
      run: mvn test
      
    - name: Login to Docker Hub
      uses: docker/login-action@v3
      with:
        username: ${{ secrets.DOCKERHUB_USERNAME }}
        password: ${{ secrets.DOCKERHUB_TOKEN }}
        
    - name: Build and Push Docker image
      id: docker_build
      uses: docker/build-push-action@v5
      with:
        context: .
        push: true
        tags: ${{ env.DOCKER_IMAGE }}:latest,${{ env.DOCKER_IMAGE }}:${{ github.sha }}

    - name: Image digest
      run: echo ${{ steps.docker_build.outputs.digest }}

  deploy:
    needs: build
    if: github.ref == 'refs/heads/master'
    runs-on: ubuntu-latest
    
    steps:
    - name: Deploy to Render
      env:
        RENDER_API_KEY: ${{ secrets.RENDER_API_KEY }}
        RENDER_SERVICE_ID: ${{ secrets.RENDER_SERVICE_ID }}
      run: |
        curl -X POST "https://api.render.com/v1/services/$RENDER_SERVICE_ID/deploys" \
          -H "accept: application/json" \
          -H "authorization: Bearer $RENDER_API_KEY" \
          -H "content-type: application/json" \
          -d '{
            "clearCache": "do_not_clear",
            "pullRequestNumber": null
          }'
        
    - name: Wait for Render Deployment
      run: |
        echo "Waiting for deployment to complete..."
        sleep 60  # Give some time for Render to start the deployment
        
    - name: Deployment Status
      run: |
        echo "🎉 Deployment triggered on Render"
        echo "Check your Render dashboard for deployment status"
        echo "Docker image used: ${{ env.DOCKER_IMAGE }}:latest"