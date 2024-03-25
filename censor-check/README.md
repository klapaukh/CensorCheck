# Censor check

This is a tool to see how much information is leaking from a censored text.

To compile it use: `mvn package` from the main directory (the one with `pom.xml`).
The tool can then be run with `java -jar .\target\censor-check-1.jar`.

The tool requires the arguments:
 - `-g` the folder which contains the ground truth files (`.ann`)
 - `-e` the folder which contains the annotation to test files (`.ann`)
 - `-t` the folder which contains the UTF-8 full text documents (`.txt`)
 - and optionally `-c` the configuration file 

Note that were a character is part of several annotations, the calculation is
done using the final annotation as listed in the `.ann` file.

## Annotation Format

All annotations for a file `xyz.txt` should be in a matching `xyz.ann` file.
The `ann` file must have each annotation of a separate line. 
Each line is formatted as: 

```
Id Category Start_Idx End_Idx match
```

 - Any kind of whitespace can be used as a separator. Also any number of 
 whitespace characters.
 - `Id` and `Category` cannot contain spaces.
 - `Id` and `match` are not used by the algorithm. They are there to make errors easier to read.
 - `Start_idx` is inclusive while `End_Idx` is exclusive.
 -  Both indexes are 0 based and count Unicode glyphs (displayed symbols), 
not characters or code points. If you are only using ASCII then these will be the same.
 - Brat `ann` notation for entities matches this format.

## Configuration

Category configuration can be done in a conf file provided by the `-c` argument.
The format of the file has each class on a separate line as:
```
CATEGORY_NAME allow=REGEX
```
If you do not have an allow regex to declare, then there is no need to declare the
`CATEGORY_NAME`. Any kind of space can be used.

Characters matched by the allow regex will always be treated as true negatives.
This stops formatting characters from reducing the scores in irrelevant to human ways.

Where a character matches the regex assigned to the special class `ALL` it will always
be considered a true negative when part of any annotation type. Regexes assigned to
other used classes have the same effect, but only for their class. If a class regex
fails it will then try the `ALL` regex. This ensures that missing non informative
parts of annotations is not penalised.

Similarly the `NONE` annotation prevents false positives from being overly penalised.
The regex for `NONE` describes the characters which will count as a true negative
even when part of what would otherwise be a false positive result. This prevents 
universally meaningless characters from bringing up the false positive rate.

## Algorithm details

The algorithm compares the number of characters that should be masked by annotation with
the number that are (as well as how many are that shouldn't be). The system uses UTF-16
chars. Which means that it will not always give sensible results (from a glyphs point of
view) for all languages, but plain English will always be fine.

Annotations will often also cover characters that do not need to be masked. E.g., consider
the name "John Doe". Our initial annotation may cover both parts of the name with a single
annotation `NAME`. This is logical, and consistent with how people read. However, suppose
an anonymiser captures the "John" and "Doe" separately. At this point the evaluation system
will report that there is 1 missed character. The space between the two words. However, this
space is non-disclosive. It could reveal the number of name parts present if the synonimser
doesn't handle that, but that seems like something safe to handle downstream. So we would
like to say that the anonymiser got all the characters correct, since the disclosure risk,
what we actually care about, has been handled.

A simple solution would then be to check any missed characters against a regex of so called
non-disclosive characters, e.g., whitespace. However this simple solution complicates comparing
statistics been different disclosive fragment detectors. Let us look at an example.

Suppose we have the following text: `Hello. My name is Inigo Montoya. You killed my father. Prepare to die!`.
The ground truth annotator creates a single annotation: `Inigo Montoya` with category `NAME`.
We then have two different algorithms under test. Both of which get the boundaries slightly wrong 
in the same way. One returns the single span `Inigo Montoy` while the other returns two spans
`Inigo` & `Montoy`. In terms of how much information they let us hide their performance is the same.
But lets have a look at what the numbers show.

#### Using all characters:
- Ground truth: `Inigo Montoya` = 13 characters
- Method 1: `Inigo Montoy` = 12 characters => recall = 12 / 13
- Method 2: `Inigo` + `Montoy` = 5 + 6 = 11 characters = 11 / 13

Using all the characters we the results show that method 2 is worse than method 1. That isn't
ideal.

#### Removing allowed skipped from the total

- Ground truth: `Inigo Montoya` = 13 characters
- Method 1: `Inigo Montoy` = 12 characters => recall = 12 / 13 = 0.923
- Method 2: `Inigo` + `Montoy` = 5 + 6 = 11 characters = 11 / (13 -1, because we skip the space) = 11 / 12 = 0.917

Now we find that method 2 is better than method 1. Still not what we want.

#### Add allowed skipped to numerator if missed

- Ground truth: `Inigo Montoya` = 13 characters (ignoring the 1 space)
- Method 1: `Inigo Montoy` = 12 characters (we now need to skip the space) => recall = 12 / 13
- Method 2: `Inigo` + `Montoy` = 5 + 6 + 1 (free space) = 12 characters = 12 / 13

These results match. But they aren't entirely satisfying as we are still counting
the contribution of the characters that we've classed as non-disclosive.

#### Removed allowed skipped from denominator and numerator globally

- Ground truth: `Inigo Montoya` = 12 characters (ignoring the 1 space)
- Method 1: `Inigo Montoy` = 11 characters (we now need to skip the space) => recall = 11 / 12
- Method 2: `Inigo` + `Montoy` = 5 + 6 = 11 characters = 11 / 12

These results also match. But this method feels more sensible as, since we 
are saying that these token don't matter, they shouldn't be part of the count at all.

#### False positives

Given that we now have a method for handling false negatives, we need to a consider a similar
scheme for false positive. In general the non-disclosive characters as part of an identifier
are not necessary characters that are okay to strip out of normal text with no loss. For example,
consider a bracket classified as a phone number. Leaving behind a bracket from a phone number may not
be a big deal, but removing from from free text may change the meaning. 

For example, consider a phone number detector running on "Sam Smith (nee Janice) attended her appointment". 
The correct answer is that there is no phone number there. However, suppose that an algorithm classified 
`(nee Janice)` as a phone number. Well you can forgive the `()` not being picked up within an actual phone
number, since they aren't really part of it. They are just formatting. But here censoring with / without them
changes what the end user can see. Language-wise there is a big difference between losing a word, and losing the
contents of a bracketed expression (since those are typically not part of the main sentence). This means that 
we cannot use categories "okay to skip" character when dealing with false positives. 


If we do something simple like strip out allowed skip characters from the annotation records where they match, that will
prevents methods from getting different scored from how they handle whitespace. Suppose `Inigo Montoya` was a false positive.
Should you be penalised 12 or 13 characters (since all of that would be lost when it shouldn't). Depending on the method,
you may benefit 12 or 13 if you get it right. And false positives should be treated the same as true positives. Otherwise
information is being valued differently in different contexts which isn't right. 

Instead we choose to leave this decision up to the user. Since this will ultimately depend on their use case.
A regex can be assigned to the category `NONE`, and any matches will always count as true negatives. I.e. their
loss is not important (consider say the detection of lone whitespace).
