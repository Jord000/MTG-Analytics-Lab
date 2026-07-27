FROM python:3.13-slim

# Don't generate .pyc files
ENV PYTHONDONTWRITEBYTECODE=1

# Flush logs straight to stdout
ENV PYTHONUNBUFFERED=1

WORKDIR /app

# Install Python dependencies
COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

# Copy application
COPY app ./app
COPY data ./data

# Run the simulator
CMD ["python", "-m", "app.main"]