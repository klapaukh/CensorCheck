# Censor check

This is a tool to see how much information is leaking from a censored text.

To compile it use: `mvn package` from the main directory (the one with `pom.xml`).
The tool can then be run with `java -jar .\target\censor-check-1.jar`.


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

#### Removed allowed skipped from denominator and numerator globally

- Ground truth: `Inigo Montoya` = 12 characters (ignoring the 1 space)
- Method 1: `Inigo Montoy` = 11 characters (we now need to skip the space) => recall = 11 / 12
- Method 2: `Inigo` + `Montoy` = 5 + 6 = 11 characters = 11 / 12

These now match which is correct.

#### Add allowed skipped to numerator if missed

- Ground truth: `Inigo Montoya` = 13 characters (ignoring the 1 space)
- Method 1: `Inigo Montoy` = 12 characters (we now need to skip the space) => recall = 12 / 13
- Method 2: `Inigo` + `Montoy` = 5 + 6 + 1 (free space) = 12 characters = 12 / 13

These also match.


#### Summary

This gives us two methods that both give result that align equally well with our needs for false negatives.
How do these two method work for false positives?

Suppose a space character is marked as a name, but it is just a white space. Should it count as a misclassification?
It's pretty easy to handle, and is the kind of error that not all parsers can generate (since may strip out spaces
when creating tokens). How about a bracket classified as a phone number? Missing a bracket from a phone number may not
be a big deal, but eating one not inside of one is a bigger issue.  

If we do something simple like strip out allowed skip characters from the annotation records where they match, that will
prevents methods from getting different scored from how they handle whitespace. Suppose `Inigo Montoya` was a false positive.
Should you be penalised 12 or 13 characters (since all of that would be lost when it shouldn't). Depending on the method,
you may benefit 12 or 13 if you get it right. And false positives should be treated the same as true positives. Otherwise
information is being valued differently in different contexts which isn't right. 

I think it makes more sense to forgive missed characters i.e., the unneeded characters like spaces are essentially given
for free when missed. Because this makes it easier to be clear as so what happens in the case of false positives (all
characters count). Unfortunately, this means that algorithms that produce split results (e.g. give the name in parts)
will typically have slightly lower false positive rates (since they don't always emit them). Otherwise false positives 
are hard to calculate. 

For example, consider a phone number detector running on "Sam Smith (nee Janice) attended her appointment". 
The correct answer is that there is no phone number there. However, suppose that an algorithm classified 
`(nee Janice)` as a phone number. Well you can forgive the `()` not being picked up within an actual phone
number, since they aren't really part of it. They are just formatting. But here censoring with / without them
changes what the end user can see. Language-wise there is a big difference between losing a word, and losing the
contents of a bracketed expression (since those are typically not part of the main sentence). But if it was a phone
number and it missed them it would get them for free as gimme points. 

Is it conceptually different if the concept was missed entirely. If you don't get the name `Inigo Montoya` should
you then be penalised for the space? Since the kind of "fill in rule" doesn't make as much sense if you didn't
even realise there was a name there in the first place. But equally the space escaping censorship isn't the problem.
It's the actual letters on either side that matter. 

Also what do we do with the skipped characters? Add them to the true negative total? Remove them from the running?

The purpose of these numbers is that they are supposed to be both meaningful to creators of algorithms, but also
to their intended purpose. If the numbers say that everything was identified and nothing was unnecessarily censored.
Those two statements go to different audience. Those who generate and own the data care about the false positives.
Justifying formatting characters being missed feels acceptable - we aren't trying to hide that information was there. Only
its actual value. While in where data is unnecessarily censored the original value of the data isn't the only thing
to consider. It also matters how the flow of language may be disrupted. Because this is for an audience that will
consume this data. In that case having a `(` classified as a phone number is a problem (even if it being missed
wouldn't be), since that token could be removed in a way that harms the rest of the data. So it is okay to treat
these cases differently. 

What characters are okay to ignore depends on the user configuration (since it can easily be problem specific).
These characters are declared against the category definitions in the configuration. There is also a special
category called `ALL` which applies to all categories. There can be categories where even missing whitespace
could be meaningful. Or where only numeric characters matter at all. Category mismatches aren't significantly
penalised. The only real false positive case is no category to assigned category. Which makes the true class
of the data `NONE` (the other special class). So we can assign a regex to the `NONE` class to specify what
characters we don't need to penalise for false positives (which will often be just whitespace, to bring word
by word and multiple word in one go methods into line).  
