# Contributing to Kuken Blueprints

Thank you for your interest in contributing to Kuken Blueprints! This guide will help you create high-quality blueprints
that provide a great experience for Kuken users.

## Table of Contents

- [Getting Started](#getting-started)
- [Blueprint Standards](#blueprint-standards)
- [Writing Guidelines](#writing-guidelines)
- [Blueprint Structure](#blueprint-structure)
- [Testing Your Blueprint](#testing-your-blueprint)
- [Submission Process](#submission-process)

## Getting Started

### Prerequisites

- PKL CLI installed (version 0.30.2 or higher)
- Understanding of Docker and containerization
- Familiarity with the application you're creating a blueprint for

### Repository Structure

```

blueprints/
├── reference/
│ └── v1/
│ └── Reference.pkl # Base blueprint schema
├── applications/
│ ├── databases/
│ │ ├── postgres/
│ │ │ └── Postgres.pkl
│ │ └── mysql/
│ │ └── MySQL.pkl
│ ├── game-servers/
│ │ └── minecraft/
│ │ └── Minecraft.pkl
│ └── web-servers/
│ └── nginx/
│ └── Nginx.pkl
└── CONTRIBUTING.md

```

## Blueprint Standards

### Naming Conventions

**File Names**

- Use PascalCase for blueprint files: `PostgreSQL.pkl`, `Minecraft.pkl`
- Match the primary application name

**Module Names**

- Follow the pattern: `io.kuken.{category}.{ApplicationName}`
- Examples: `io.kuken.databases.PostgreSQL`, `io.kuken.gameservers.Minecraft`

**Input IDs**

- Use kebab-case: `admin-password`, `http-port`, `max-memory`
- Be descriptive and concise (2-64 characters)

**Input Labels**

- Use title case: "Admin Password", "HTTP Port", "Maximum Memory"
- Be user-friendly and avoid technical jargon when possible

**Environment Variables**

- Use UPPER_SNAKE_CASE: `DATABASE_URL`, `MAX_CONNECTIONS`, `ADMIN_PASSWORD`
- Follow the application's official documentation naming

### Docker Image Selection

**Use Official Images When Available**

```pkl
docker {
  image = "postgres:16-alpine"  // ✓ Official PostgreSQL image
}
```

**For Community Images**

```pkl
docker {
  image = "itzg/minecraft-server:latest"  // ✓ Well-maintained community image
}
```

**Guidelines**

- Pin to specific versions or tags for stability
- Prefer Alpine variants when available for smaller image sizes
- Use well-maintained images with regular security updates
- Document the image source in comments

## Writing Guidelines

### Documentation Style

**Be Concise and Direct**

```pkl
/// Maximum memory allocation for the server process.
/// Specify as "2G", "4096M", etc. Minimum 1GB recommended.
local MaxMemory = new TextInput {
    name = "max-memory"
    label = "Maximum Memory"
  }
```

**Avoid Over-Explaining**

```pkl
// ❌ Too verbose
/// This is the maximum memory allocation setting for the Java Virtual Machine
/// that runs the Minecraft server. It controls how much RAM the server can use.
/// You should set this based on your available system resources and the number
/// of expected players. For small servers with 2-5 players, 2GB is sufficient...

// ✓ Concise and helpful
/// Maximum memory for the Java process.
/// Recommended: 2GB for small servers (2-5 players), 4GB+ for larger servers.
```

**Use Markdown Formatting**

- Use backticks for values: `` `"latest"` ``, `` `8080` ``
- Use bold sparingly for emphasis: **Required**, **Important**
- Use code blocks only for multi-line examples

### Input Design

**Provide Sensible Defaults**

```pkl
local HttpPort = new PortInput {
  name = "http-port"
  label = "HTTP Port"
  default = 8080  // ✓ Common alternative HTTP port
}
```

**Group Related Inputs**

```pkl
inputs {
  // Application Configuration
  AppVersion
  ServerType

  // Security
  AdminPassword
  ApiKey

  // Resources
  MaxMemory
  MaxConnections

  // Network
  HttpPort
  RconPort
}
```

**Use Appropriate Input Types**

- `TextInput`: Usernames, versions, non-sensitive text
- `PasswordInput`: Passwords, API keys, secrets
- `PortInput`: Network ports with validation

### Environment Variables

**Follow Application Conventions**

```pkl
// ✓ Use the application's documented variable names
env("POSTGRES_PASSWORD", AdminPassword)
env("POSTGRES_USER", "postgres")
env("POSTGRES_DB", DatabaseName)

// ❌ Don't invent your own naming
env("DB_PASS", AdminPassword)
env("DB_USERNAME", "postgres")
```

**Order Variables Logically**

```pkl
environmentVariables {
  // Required Configuration
  env("EULA", "TRUE")
  env("TYPE", ServerType)

  // Authentication
  env("ADMIN_PASSWORD", AdminPassword)

  // Network
  env("SERVER_PORT", 25565)

  // Resources
  env("MEMORY", MaxMemory)
}
```

**Use Runtime References When Needed**

```pkl
// ✓ Use refs for runtime-generated values
env("CONTAINER_NAME", refs.instance.name)
env("INSTANCE_ID", refs.instance.id)

// ❌ Don't hardcode instance-specific values
env("CONTAINER_NAME", "my-container")
```

## Blueprint Structure

### Standard Template

```pkl
/// # Application Name
///
/// Brief description of what this blueprint deploys.
/// Include key features or notable configuration options.
///
/// **Docker Image:** {image-name}
///
/// **Repository:** {github-or-docs-url}
module io.kuken.{category }.{ApplicationName }

amends "file://path/to/Reference.pkl"

// ============================================================================
// METADATA
// ============================================================================

name = "Application Display Name"
version = "1.0.0"
url = "https://official-application-website.com"

// ============================================================================
// INPUT DEFINITIONS
// ============================================================================

/// Brief description of what this input configures.
/// Include examples or recommendations if helpful.
local InputName = new TextInput {
  name = "input-id"
  label = "Display Label"
}

// Define all inputs here...

// ============================================================================
// BUILD CONFIGURATION
// ============================================================================

build {
  docker {
    image = "official/image:tag"
  }

  environmentVariables {
    // Group related variables with comments
    env("REQUIRED_VAR", "value")
    env("USER_INPUT_VAR", InputReference)
    env("RUNTIME_VAR", refs.instance.name)
  }
}

// ============================================================================
// USER INPUTS
// ============================================================================

inputs {
  // List inputs in logical order for user experience
  InputName
  AnotherInput
}
```

### Complete Example: Redis Blueprint

```pkl
/// # Redis
///
/// In-memory data structure store used as a database, cache, and message broker.
/// This blueprint deploys Redis with optional password protection and custom port configuration.
///
/// **Docker Image:** redis:alpine
///
/// **Repository:** https://github.com/redis/redis
module io.kuken.databases.Redis

amends "file:///blueprints/reference/v1/Reference.pkl"

// ============================================================================
// METADATA
// ============================================================================

name = "Redis"
version = "1.0.0"
url = "https://redis.io"

// ============================================================================
// INPUT DEFINITIONS
// ============================================================================

/// Redis server version tag.
/// Common values: `"7-alpine"`, `"6.2-alpine"`, `"latest"`.
local RedisVersion = new TextInput {
  name = "redis-version"
  label = "Redis Version"
}

/// Password for Redis authentication.
/// Leave empty for no authentication (not recommended for production).
local RedisPassword = new PasswordInput {
  name = "redis-password"
  label = "Redis Password"
}

/// Redis server port.
/// Standard Redis port is 6379.
local RedisPort = new PortInput {
  name = "redis-port"
  label = "Redis Port"
  default = 6379
}

/// Maximum memory limit for Redis.
/// Format: `"256mb"`, `"1gb"`, etc. Redis will evict keys when limit is reached.
local MaxMemory = new TextInput {
  name = "max-memory"
  label = "Maximum Memory"
}

// ============================================================================
// BUILD CONFIGURATION
// ============================================================================

build {
  docker {
    image = "redis:${RedisVersion}"
  }

  environmentVariables {
    // Authentication
    env("REDIS_PASSWORD", RedisPassword)

    // Network
    env("REDIS_PORT", RedisPort)

    // Resources
    env("REDIS_MAXMEMORY", MaxMemory)
    env("REDIS_MAXMEMORY_POLICY", "allkeys-lru")
  }
}

// ============================================================================
// USER INPUTS
// ============================================================================

inputs {
  RedisVersion
  RedisPassword
  RedisPort
  MaxMemory
}
```

## Testing Your Blueprint

### Validation

**Check Syntax**

```bash
pkl eval your-blueprint.pkl
```

**Validate Against Reference**

```bash
pkl test your-blueprint.pkl
```

### Pre-Submission Checklist

- [ ] Blueprint follows naming conventions
- [ ] All required fields are present (name, version, url, inputs, build, refs)
- [ ] Documentation is concise and helpful
- [ ] Input IDs use kebab-case
- [ ] Input labels are user-friendly
- [ ] Environment variables follow application conventions
- [ ] Docker image is official or well-maintained
- [ ] Sensible defaults are provided for port inputs
- [ ] No sensitive information is hardcoded
- [ ] Module name follows `io.kuken.{category}.{ApplicationName}` pattern

## Submission Process

### 1. Fork and Clone

```bash
git clone https://github.com/YOUR-USERNAME/kuken-blueprints.git
cd kuken-blueprints
```

### 2. Create a Feature Branch

```bash
git checkout -b blueprint/application-name
```

### 3. Create Your Blueprint

Place your blueprint in the appropriate category:

```
blueprints/applications/{category}/{application-name}/ApplicationName.pkl
```

### 4. Test Locally

```bash
pkl eval blueprints/applications/{category}/{application-name}/ApplicationName.pkl
```

### 5. Commit and Push

```bash
git add blueprints/applications/{category}/{application-name}/
git commit -m "Add {ApplicationName} blueprint"
git push origin blueprint/application-name
```

### 6. Create Pull Request

- Use a clear title: "Add {ApplicationName} blueprint"
- Describe what the blueprint does
- Include any special configuration notes
- Reference the official application documentation

### Pull Request Template

```markdown
## Blueprint Information

**Application:** [Application Name]
**Category:** [databases/web-servers/game-servers/etc]
**Docker Image:** [image:tag]
**Official Documentation:** [URL]

## Description

Brief description of what this blueprint deploys and any notable features.

## Testing

- [ ] Blueprint validates with `pkl eval`
- [ ] All required inputs are present
- [ ] Documentation is complete
- [ ] Follows contribution guidelines

## Additional Notes

Any special considerations or configuration notes for reviewers.
```

## Blueprint Categories

Organize your blueprint under the appropriate category:

- `databases/` - PostgreSQL, MySQL, MongoDB, Redis, etc.
- `web-servers/` - NGINX, Apache, Caddy, etc.
- `game-servers/` - Minecraft, Valheim, ARK, etc.
- `monitoring/` - Prometheus, Grafana, etc.
- `messaging/` - RabbitMQ, Kafka, etc.
- `development/` - GitLab, Jenkins, etc.
- `media/` - Plex, Jellyfin, etc.

If your blueprint doesn't fit existing categories, propose a new one in your pull request.

## Getting Help

- **Issues:** Open an issue for questions or problems
- **Discussions:** Join discussions for blueprint ideas
- **Documentation:** Check the main Kuken documentation at https://kuken.io/docs

## Code of Conduct

Be respectful, constructive, and collaborative. We're building this together to help the community deploy applications
easily.

## License

By contributing, you agree that your contributions will be licensed under the same license as the Kuken project.

---

Thank you for contributing to Kuken Blueprints! Your work helps others deploy applications quickly and reliably.