[![MIT License][license-shield]][license-url]
[![Maven Central](https://img.shields.io/maven-central/v/io.github.ajclopez/mongo-spring-search.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22io.github.ajclopez%22%20AND%20a:%22mongo-spring-search%22)

<p align="center">
  <h3 align="center">Mongo Spring Search</h3>
  <p align="center">
    Mongo Spring Search provides a query language to a MongoDB database.
    <br />
    <a href="https://github.com/ajclopez/mongo-spring-search#usage"><strong>Explore the docs</strong></a>
    <br />
    <br />
    <a href="https://github.com/ajclopez/mongo-spring-search/issues">Report Bug</a>
    ·
    <a href="https://github.com/ajclopez/mongo-spring-search/issues">Request Feature</a>
  </p>
</p>

# Mongo Spring Search



### Content index

* [What is this?](#what-is-this)
* [Getting Started](#getting-started)
    *  [Installation](#installation)
* [Usage](#usage)
* [Supported features](#supported-features)
    * [Filtering](#filtering)
    * [Pagination](#pagination)
    * [Sorting](#sorting)
    * [Projection](#projection)
    * [Advanced queries](#advanced-queries)
* [Available options](#available-options)
    * [Customize limit value](#customize-limit-value)
    * [Specify casting per param keys](#specify-casting-per-param-keys)
* [Contributing](#contributing)
* [License](#license)

## What is this?

Mongo Spring Search provides a simple query language to perform advanced searches for your collections in **MongoDB**.

You could create custom repository methods to perform your searches. You could also use **Mongo Spring Search** to searching, sorting, pagination and combining logical operators.

## Getting Started

To get a local copy and run it, follow these simple steps.

### Installation
1. Clone the repo
```sh
git clone https://github.com/ajclopez/mongo-spring-search.git
```

2. Make the library available
```sh
mvn install #inside the mongo-spring-search folder
```

3. Add the repo to your project inside your `pom.xml` file
```xml
<dependency>
    <groupId>io.github.ajclopez</groupId>
    <artifactId>mongo-spring-search</artifactId>
    <version>1.0.0</version>
</dependency>
```

## Usage

Converts query into a MongoDB query object.

```java
MongoSpringSearch.mss(String query)

MongoSpringSearch.mss(String query, Optional<Configuration> configuration)
```

##### Arguments
`query`: query string part of the requested API URL.

`configuration`: object for advanced configuration (See below) [optional].

You can add custom methods to your repository:

```java
@Repository
public interface YourRepository extends MongoRepository<YourDocument, String>, YourCustomRepository {
}
```

To expand the `Spring MongoRepository` repository methods you can do it in the following way:

```java
public interface YourCustomRepository {
	public List<YourDocument> findAll(String query);
}
```

In this class you can use `Mongo Spring Search`:

```java
public class YourCustomRepositoryImpl implements YourCustomRepository {

	private MongoTemplate mongoTemplate;
	
	@Autowired
	public YourCustomRepositoryImpl(MongoTemplate mongoTemplate) {
		this.mongoTemplate = mongoTemplate;
	}
	
	@Override
	public List<YourDocument> findAll(String query) {
		return mongoTemplate.find(MongoSpringSearch.mss(query), YourDocument.class);
	}
}
```

Use it in your controller:

```java
@GetMapping
public ResponseEntity<List<YourResponse>> yourFunctionNameHere(HttpServletRequest request) {
	return ResponseEntity.ok().body(yourRepository.findAll(request.getQueryString()));
}
```

##### Example

```java
Query query = MongoSpringSearch.mss("status=sent&date>2020-01-06T14:00:00.000Z&author.firstname=Jhon&skip=50&limit=100&sort=-date&fields=id,date");
```

## Supported features

### Filtering

| Operator      	| URI               	| Example                        	|
| -----------------	| ---------------------	| ---------------------------------	| 
| `$eq`          	| `key=val`				| `type=public`        				|
| `$ne`          	| `key!=val`        	| `status!=SENT`                    |
| `$gt`          	| `key>val`             | `price>=5`                        |
| `$gte`         	| `key>=val`            | `price>==9`                       |
| `$lt`          	| `key<val`             | `date<2020-01-01T14:00:00.000Z`   |
| `$lte`         	| `key<=val`            | `priority<=-5`                    |
| `$in`          	| `key=val1,val2`       | `status=QUEUED,DEQUEUED`          |
| `$nin`          	| `key!=val1,val2`      | `status!=QUEUED,DEQUEUED`         |
| `$exists`         | `key`          		| `email`              				|
| `$exists`         | `!key`         		| `!email`                    		|
| `$regex`      	| `key=/value/<opts>`	| `email=/@gmail\.com$/`			|
| `$regex`        	| `key!=/value/<opts>`  | `phone!=/^58/`                    |


### Pagination

Useful to limit the number of records returned.

- Operator keys are `skip` and `limit`.
- Use `limit` operator to limit the number of records returned.
- Use `skip` operator to skip the specified number of records.

```json
skip=20&limit=10
```

### Sorting

Useful to sort returned records.

- Operator key is `sort`. 
- It accepts a comma-separated list of fields.
- Use `-` prefixes to sort in descending order.
- Use `+` prefixes to sort in ascedending order.

```json
sort=id,-date
```

### Projection

Useful to limit fields to return in each records.

- Operator key is `fields`.
- It accepts a comma-separated list of fields.

```json
fields=firstname,lastname,phone,email
```

**Note:** 
* The `_id` field (returned by default).


### Advanced queries

For more advanced usage (`and`, `or` logic operations), pass query `filter` as string with the logical operations, for example:

```json
filter=(country=Mexico OR country=Spain) and gender=female
````

##### What operations are possible?

* Filtering operations.
* The `AND/and` operator.
* The `OR/or` operator.
* Parenthesis can be used for grouping.

## Available options

You can use advanced options:

```java
Configuration(Map<String, CastType> casters, Integer defaultLimit, Integer maxLimit)
```

* `casters` object to specify custom casters, key is the caster name, and value is a type (`BOOLEAN, NUMBER, PATTERN, DATE, STRING`).
* `defaultLimit` which contains custom value to return records.
* `maxLimit` which contains custom value to return a maximum of records.

### Customize limit value

You can specify your own maximum or default limit value.

* `defaultLimit`: custom value to return records.
* `maxLimit`: custom value to return a maximum of records.

```java
Configuration options = new Configuration(null, 10, 500);

MongoSpringSearch.mss("organizationId=123&skip=10&limit=1000", Optional.of(options));
```

### Specify casting per param keys

You can specify how query parameter values are casted by passing an object.

* `casters`: object which map keys to casters.

```java
Map<String, CastType> casters = new HashMap<String, CastType>();
casters.put("key1", CastType.STRING);
casters.put("key2", CastType.NUMBER);
casters.put("key3", CastType.STRING);
casters.put("key4", CastType.BOOLEAN);
		
Configuration options = new Configuration(casters, null, null);
MongoSpringSearch.mss("key1=VALUE&key2=10&key3=20&key4=true", Optional.of(options));
```

## Contributing

Should you like to provide any feedback, please open up an Issue, I appreciate feedback and comments. Any contributions you make are **greatly appreciated**.

1. Fork the Project
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing-feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License

This software is released under the MIT license. See `LICENSE` for more information.


[license-shield]: https://img.shields.io/badge/License-MIT-yellow.svg
[license-url]: https://github.com/ajclopez/mongo-spring-search/blob/master/LICENSE.txt

