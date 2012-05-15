/* Copyright (C) 2008-2010 University of Massachusetts Amherst,
   Department of Computer Science.
   This file is part of "FACTORIE" (Factor graphs, Imperative, Extensible)
   http://factorie.cs.umass.edu, http://code.google.com/p/factorie/
   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at
    http://www.apache.org/licenses/LICENSE-2.0
   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License. */

package cc.factorie
import java.util.BitSet
import scala.util.Random

//package object maths 
//extends maths.Probit with 
//maths.LogGamma with 
//maths.Poly with 
//maths.Factorial with 
//maths.Binomial with
//maths.Combinatorics with
//maths.LogProb with 
//maths.Trig with 
//maths.Close with
//maths.RandomDistributions with
//maths.ArrayOps with
//maths.ArrayImplicits with
//maths.IndexedSeqOps with
//maths.IndexedSeqImplicits

// TODO Once cc.factorie.optimze uses la.Tensor, then remove "extends ArrayOps"
// Consider also implicit class DenseTensor1(a:Array[Double])
package object maths extends ArrayOps {
  
  // TODO This will get removed also when cc.factorie.optimize uses la.Tensor
  trait ArrayWrapper {
    type A = Array[Double]
    def s: Array[Double]
    //def length = s.length
    //def apply(i:Int) = s(i)
    //def update(i:Int, d:Double): Unit = s(i) = d
    def oneNorm: Double = ArrayOps.oneNorm(s)
    def twoNorm: Double = ArrayOps.twoNorm(s)
    def twoNormSquared: Double = ArrayOps.twoNormSquared(s)
    def infinityNorm: Double = ArrayOps.infinityNorm(s)
    def +=(d: Double): Unit = ArrayOps.+=(s, d)
    def -=(d: Double): Unit = ArrayOps.-=(s, d)
    def *=(d: Double): Unit = ArrayOps.*=(s, d)
    def /=(d: Double): Unit = ArrayOps./=(s, d)
    def incr(t: A): Unit = ArrayOps.incr(s, t)
    def incr(t: A, factor: Double): Unit = ArrayOps.incr(s, t, factor)
    def different(t: A, threshold:Double): Boolean = ArrayOps.different(s, t, threshold)
    def dot(t: A): Double = ArrayOps.dot(s, t)
    def normalize(): Double = ArrayOps.normalize(s)
    def oneNormalize(): Double = ArrayOps.oneNormalize(s)
    def twoNormalize(): Double = ArrayOps.twoNormalize(s)
    def twoSquaredNormalize(): Double = ArrayOps.twoSquaredNormalize(s)
    def absNormalize(): Double = ArrayOps.absNormalize(s)
    def expNormalize(): Double = ArrayOps.expNormalize(s)
    def normalizeLogProb(): Double = ArrayOps.normalizeLogProb(s)
    def contains(d: Double): Boolean = ArrayOps.contains(s, d)
    def maxIndex: Int = ArrayOps.maxIndex(s)
    def isNaN: Boolean = ArrayOps.isNaN(s)
    def substitute(oldValue: Double, newValue: Double): Unit = ArrayOps.substitute(s, oldValue, newValue)
    def copy: Array[Double] = ArrayOps.copy(s)
    def set(t: A): Unit = ArrayOps.set(s, t)
  }

  //implicit def array2ArrayOps(toWrap: Array[Double]): ArrayWrapper = new ArrayWrapper { val s = toWrap }


  
  
  
  
  
  

// Binomial

   /** Computes p(x;n,p) where x~B(n,p)  */
  // Copied as the "classic" method from Catherine Loader.
  //  Fast and Accurate Computation of Binomial Probabilities.
  //   2001.  (This is not the fast and accurate version.)
  def logBinom(x:Int, n:Int, p:Double): Double = {
    logFactorial (n) - logFactorial (x) - logFactorial (n - x)
      + (x*math.log (p)) + ((n-x)*math.log (1-p))
   }

  /** Vastly inefficient O(x) method to compute cdf of B(n,p)  */
  def pbinom (x:Int, n:Int, p:Double): Double = {
    var sum: Double = Double.NegativeInfinity
    for (i <- 0 to x) sum = sumLogProb(sum, logBinom(i, n, p))
    math.exp(sum)
  }



// Close

  /** Numbers that are closer than this are considered equal */
  def almostEquals (d1:Double, d2:Double, epsilon:Double = 0.000001) : Boolean = math.abs (d1 - d2) < epsilon

  //@deprecated("Use DoubleSeq.different instead")
  //def almostEquals (d1:Array[Double], d2:Array[Double], eps:Double) : Boolean = {
  //  for (i <- 0 until d1.length) if (!almostEquals(d1(i), d2(i))) return false
  //  true
  //}

  /* given two DoubleSeq calculate the L2 distance */
  //@deprecated("Use DoubleSeq.l2Similarity instead")
  //def L2(a:DoubleSeq[Double], b:Seq[Double]): Double = {
  //  assert(a.size == b.size)
  //  var sum = 0.0
  //  for (i <- 0 until a.size) sum += (a(i)-b(i))*(a(i)-b(i))
  //  math.sqrt(sum)
  //}

  // gsc
  /** Checks if <tt>min &lt;= value &lt;= max</tt>. */
  //def isWithinRange(value:Double, min:Double, max:Double, epsilon: Double = 0.000001) =
  //  (value > min || almostEquals(value, min, epsilon)) &&
  //  (value < max || almostEquals(value, max, epsilon));





// Combinatorics

  def numCombinations(n:Int, r:Int) = math.exp (logFactorial(n)-logFactorial(r)-logFactorial(n-r))
  def numPermutations (n:Int, r:Int) = math.exp (logFactorial(n)-logFactorial(r))




// Factorial

object FactorialCache {
  val size = 13 // 12! = 479 001 600, 13! = 6 227 020 800, java.Integer.MAX_INT = (2^31) - 1 = 2 147 483 647
  private val cache = new Array[Int](size)
  cache(0) = 1; for (i <- 1 until size) cache(i) = i * cache(i-1)
  def factorial(n:Int): Int = cache(n)
}

  def factorial(n:Int): Double = if (n < FactorialCache.size) FactorialCache.factorial(n) else math.exp(logGamma(n+1.0))
  def logFactorial(n:Int): Double = logGamma(n+1.0)




// LogGamma

object LogGamma {
  val d1 = -5.772156649015328605195174e-1;
  val p1 = Array( 
    4.945235359296727046734888e0, 2.018112620856775083915565e2, 
    2.290838373831346393026739e3, 1.131967205903380828685045e4, 
    2.855724635671635335736389e4, 3.848496228443793359990269e4, 
    2.637748787624195437963534e4, 7.225813979700288197698961e3 
  )
  val q1 = Array(
    6.748212550303777196073036e1, 1.113332393857199323513008e3, 
    7.738757056935398733233834e3, 2.763987074403340708898585e4, 
    5.499310206226157329794414e4, 6.161122180066002127833352e4, 
    3.635127591501940507276287e4, 8.785536302431013170870835e3
  )
  val d2 = 4.227843350984671393993777e-1
  val p2 = Array(
    4.974607845568932035012064e0, 5.424138599891070494101986e2, 
    1.550693864978364947665077e4, 1.847932904445632425417223e5, 
    1.088204769468828767498470e6, 3.338152967987029735917223e6, 
    5.106661678927352456275255e6, 3.074109054850539556250927e6
  )
  val q2 = Array(
    1.830328399370592604055942e2, 7.765049321445005871323047e3, 
    1.331903827966074194402448e5, 1.136705821321969608938755e6, 
    5.267964117437946917577538e6, 1.346701454311101692290052e7, 
    1.782736530353274213975932e7, 9.533095591844353613395747e6
  )
  val d4 = 1.791759469228055000094023e0;
  val p4 = Array(
    1.474502166059939948905062e4, 2.426813369486704502836312e6, 
    1.214755574045093227939592e8, 2.663432449630976949898078e9, 
    2.940378956634553899906876e10, 1.702665737765398868392998e11, 
    4.926125793377430887588120e11, 5.606251856223951465078242e11
  )
  val q4 = Array(
    2.690530175870899333379843e3, 6.393885654300092398984238e5, 
    4.135599930241388052042842e7, 1.120872109616147941376570e9, 
    1.488613728678813811542398e10, 1.016803586272438228077304e11, 
    3.417476345507377132798597e11, 4.463158187419713286462081e11
  )
  val c = Array(
    -1.910444077728e-03, 8.4171387781295e-04, 
    -5.952379913043012e-04, 7.93650793500350248e-04, 
    -2.777777777777681622553e-03, 8.333333333333333331554247e-02, 
    5.7083835261e-03
  )
}

  // From libbow, dirichlet.c
  // Written by Tom Minka <minka@stat.cmu.edu>
  def logGamma (xa:Double) : Double = {
    import LogGamma._
    var x = xa
    var result = 0.0; var y = 0.0; var xnum = 0.0; var xden = 0.0
    var i = 0
    val a = 0.6796875;

    if((x <= 0.5) || ((x > a) && (x <= 1.5))) {
      if(x <= 0.5) {
        result = -math.log(x);
        /*  Test whether X < machine epsilon. */
        if(x+1 == 1) {
          return result;
        }
      }
        else {
          result = 0;
          x = (x - 0.5) - 0.5;
        }
      xnum = 0;
      xden = 1;
      for(i <- 0 until 8) {
        xnum = xnum * x + p1(i);
        xden = xden * x + q1(i);
      }
      result += x*(d1 + x*(xnum/xden));
    }
        else if((x <= a) || ((x > 1.5) && (x <= 4))) {
          if(x <= a) {
            result = -math.log(x);
            x = (x - 0.5) - 0.5;
          }
          else {
            result = 0;
            x -= 2;
          }
          xnum = 0;
          xden = 1;
          for(i <- 0 until 8) {
            xnum = xnum * x + p2(i);
            xden = xden * x + q2(i);
          }
          result += x*(d2 + x*(xnum/xden));
        }
          else if(x <= 12) {
            x -= 4;
            xnum = 0;
            xden = -1;
            for(i <- 0 until 8) {
              xnum = xnum * x + p4(i);
              xden = xden * x + q4(i);
            }
            result = d4 + x*(xnum/xden);
          }
    /*  X > 12  */
          else {
            y = math.log(x);
            result = x*(y - 1) - y*0.5 + .9189385332046727417803297;
            x = 1/x;
            y = x*x;
            xnum = c(6);
            for(i <- 0 until 6) xnum = xnum * y + c(i);
            xnum *= x;
            result += xnum;
          }
    return result;
  }
  
  def logBeta(a:Double , b:Double) = logGamma(a)+logGamma(b)-logGamma(a+b)
  def beta(a:Double, b:Double) = math.exp(logBeta(a,b))
  def gamma (x:Double) = math.exp(logGamma(x))

  
  

// Log Prob
  
  val log2 = math.log(2);

  /** Return the entropy of the normalized distribution p.
      The log is w.r.t. base 2.  */
  def entropy(p:Seq[Double]): Double = {
    var result = 0.0
    var i = p.length - 1
    var pv = 0.0
    while (i >= 0) {
      pv = p(i)
      require(pv >= 0.0, pv)
      require(pv <= 1.000001)
      if (pv > 0.0)
        result -= pv * math.log(pv)
      i -= 1
    }
    result / log2
  }

  /**
   * Returns the KL divergence, K(p1 || p2).
   * The log is w.r.t. base 2. <p>
   * *Note*: If any value in <tt>p2</tt> is <tt>0.0</tt> then the KL-divergence
   * is <tt>infinite</tt>. 
   */
  def klDivergence(p1:Seq[Double], p2:Seq[Double]) = {
    assert(p1.length == p2.length);
    var klDiv = 0.0;
    for (i <- 0 until p1.length) {
      if (p1(i) != 0.0)
        klDiv += p1(i) * math.log(p1(i) / p2(i))
    }
    klDiv / log2
  }

  /** Returns the Jensen-Shannon divergence. */
  def jensenShannonDivergence(p1:Seq[Double], p2:Seq[Double]) = {
    assert(p1.length == p2.length);
    val average = new Array[Double](p1.length);
    for (i <- 0 until p1.length) average(i) += (p1(i) + p2(i)) / 2.0
    (klDivergence(p1, average) + klDivergence(p2, average)) / 2.0
  }

  /**
   *  Returns the sum of two doubles expressed in log space,
   *   that is,
   * <pre>
   *    sumLogProb = log (e^a + e^b)
   *               = log e^a(1 + e^(b-a))
   *               = a + log (1 + e^(b-a))
   * </pre>
   *
   * By exponentiating <tt>b-a</tt>, we obtain better numerical precision than
   *  we would if we calculated <tt>e^a</tt> or <tt>e^b</tt> directly.
   * <P>
   * Note: This function is just like 
   *  {@link cc.mallet.fst.Transducer#sumNegLogProb sumNegLogProb}
   *   in <TT>Transducer</TT>,
   *   except that the logs aren't negated.
   */
  def sumLogProb (a:Double, b:Double) =   {
    if (a == Double.NegativeInfinity) 
      b
    else if (b == Double.NegativeInfinity)
      a
    else if (b < a)
      a + math.log1p(math.exp(b-a))
    else
      b + math.log1p(math.exp(a-b))
  }

  // Below adapted from Stanford NLP package, SloppyMath.java
  /**
   * Sums an array of numbers log(x1)...log(xn).  This saves some of
   *  the unnecessary calls to Math.log in the two-argument version.
   * <p>
   * Note that this implementation IGNORES elements of the input
   *  array that are more than LOGTOLERANCE (currently 30.0) less
   *  than the maximum element.
   * <p>
   * Cursory testing makes me wonder if this is actually much faster than
   *  repeated use of the 2-argument version, however -cas.
   * @param vals An array log(x1), log(x2), ..., log(xn)
   * @return log(x1+x2+...+xn)
   */
  def sumLogProbs (vals:Seq[Double]): Double = {
    val LOGTOLERANCE = 30.0

    val len = vals.length
    var max = vals(0)
    var maxIdx = 0
    var i = 1
    while (i < len) {
      val v = vals(i)
      if (v > max) {
        max = v
        maxIdx = i
      }
      i += 1
    }
    var anyAdded = false
    var intermediate = 0.0
    val cutoff = max - LOGTOLERANCE
    i = 0
    while (i < len) {
      if (vals(i) >= cutoff && i != maxIdx) {
        anyAdded = true
        intermediate += math.exp(vals(i) - max)
      }
      i += 1
    }
    if (anyAdded)
      max + math.log1p(intermediate)
    else
      max
  }

  /**
   *  Returns the difference of two doubles expressed in log space,
   *   that is,
   * <pre>
   *    sumLogProb = log (e^a - e^b)
   *               = log e^a(1 - e^(b-a))
   *               = a + log (1 - e^(b-a))
   * </pre>
   *
   * By exponentiating <tt>b-a</tt>, we obtain better numerical precision than
   *  we would if we calculated <tt>e^a</tt> or <tt>e^b</tt> directly.
   * <p>
   * Returns <tt>NaN</tt> if b > a (so that log(e^a - e^b) is undefined).
   */
  def subtractLogProb (a:Double, b:Double) = 
    if (b == Double.NegativeInfinity) a else a + math.log (1 - math.exp(b-a))

    
    


// Poly

  def poly(coeff: Array[Double], x: Double): Double = {
    var result: Double = 0;
    var i: Int = coeff.length - 1;
    while (i >= 0) {
      result = result * x + coeff(i);
      i -= 1;
    }
    result;
  }




// Probit

object Probit {
  val a = Array(
    3.3871328727963666080,
    133.14166789178437745,
    1971.5909503065514427,
    13731.693765509461125,
    45921.953931549871457,
    67265.770927008700853,
    33430.575583588128105,
    2509.0809287301226727
  )
  val b = Array(
    1.0,
    42.313330701600911252,
    687.18700749205790830,
    5394.1960214247511077,
    21213.794301586595867,
    39307.895800092710610,
    28729.085735721942674,
    5226.4952788528545610
  )
  val c = Array(
    1.42343711074968357734,
    4.63033784615654529590,
    5.76949722146069140550,
    3.64784832476320460504,
    1.27045825245236838258,
    0.241780725177450611770,
    0.0227238449892691845833,
    0.00077454501427834140764
  )
  val const1 = 0.180625;
  val const2 = 1.6;
  val d = Array(
    1.0E+00,
    2.05319162663775882187E+00,
    1.67638483018380384940E+00,
    6.89767334985100004550E-01,
    1.48103976427480074590E-01,
    1.51986665636164571966E-02,
    5.47593808499534494600E-04,
    1.05075007164441684324E-09
  )
  val e = Array(
    6.65790464350110377720E+00,
    5.46378491116411436990E+00,
    1.78482653991729133580E+00,
    2.96560571828504891230E-01,
    2.65321895265761230930E-02,
    1.24266094738807843860E-03,
    2.71155556874348757815E-05,
    2.01033439929228813265E-07
  )
  val f = Array(
    1.0E+00,
    5.99832206555887937690E-01,
    1.36929880922735805310E-01,
    1.48753612908506148525E-02,
    7.86869131145613259100E-04,
    1.84631831751005468180E-05,
    1.42151175831644588870E-07,
    2.04426310338993978564E-15
  )
  val split1 = 0.425;
  val split2 = 5.0;
}

  def probit(p: Double): Double = {
    import Probit._
    if (p <= 0)
      return Double.NegativeInfinity
    if (p >= 1)
      return Double.PositiveInfinity
    val q = p - 0.5;
    var r: Double = 0;
    var g: Double = 0;
    if (math.abs(q) <= split1) {
      //System.out.println("CASE 1" );
      r = const1 - q * q;
      g = q * poly(a, r) / poly(b, r);
    } else {
      //System.out.println("CASE 2" );
      if (q < 0) r = p
      else r = 1 - p
      if (r <= 0) g = -1;
      else {
        //System.out.println("  (b)");
        r = math.sqrt(-math.log(r));
        if (r <= split2) {
          //System.out.println("   (i)");
          r = r - const2
          g = poly(c, r) / poly(d, r)
        } else {
          //System.out.println("   (ii)");
          r = r - split2
          g = poly(e, r) / poly(f, r)
        }
        //r=r-split2
        if (q < 0) g = -g
      }
    }
    g
  }

  def sigmod(beta:Double) = 1.0/(1.0+math.exp(-beta))
  def sigmod_rev(sig:Double) = math.log(sig/(1-sig))
  def logit(p:Double) = math.log (p / (1 - p))




// Random Distributions

  implicit val implicitRandom:Random = cc.factorie.random
 
  /** Return random integer from Poission with parameter lambda.  
   * The mean of this distribution is lambda.  The variance is lambda. */
  def nextPoisson(lambda:Double)(implicit r:Random) : Double = {
    var v = -1
    val l=math.exp(-lambda)
    var p=1.0;
    while (p>=l) { p *= nextUniform(r); v += 1 }
    v
  }

  /** Return nextPoisson(1). */
  def nextPoisson(implicit r:Random) : Double = nextPoisson(1)(r)

  /** Return a random boolean, equally likely to be true or false. */
  def nextBoolean(implicit r:Random) = r.nextBoolean
  // TODO  Is this good enough?  Code used to be:
  // return (next(32) & 1 << 15) != 0;


  /** Return a random boolean, with probability p of being true. */
  def nextBoolean(p:Double)(implicit r:Random) = nextUniform(r) < p

  /** Return a random BitSet with "size" bits, each having probability p of being true. */
  def nextBitSet (size:Int, p:Double)(implicit r:Random) : BitSet = {
    val bs = new BitSet (size)
    for (i <- 0 until size) if (nextBoolean(p)(r)) bs.set(i)
    bs
  }

  /** Return a random double in the range 0 to 1, inclusive, uniformly sampled from that range. 
   * The mean of this distribution is 0.5.  The variance is 1/12. */
  @inline final def nextUniform(implicit r:Random) : Double = r.nextDouble
  // Is this good enough?  Code used to be:
  // val l = ((r.self.next(26)).asInstanceOf[Long] << 27) + next(27);
  // l / (1L << 53).asInstanceOf[Double]

  /** Return a random double in the range a to b, inclusive, uniformly sampled from that range.
   * The mean of this distribution is (b-a)/2.  The variance is (b-a)^2/12 */
  //def nextUniform(a:Double, b:Double)(implicit r:Random) : Double = a + (b-a)*nextUniform(r)

  /** Draw a single sample from multinomial "a".  Assumes that the elements of "a" already sum to 1.0. */
  def nextDiscrete (a:Array[Double])(implicit r:Random): Int = {
    var b = 0.0; val s = nextUniform(r); var i = 0
    while (b <= s && i < a.length) { assert (a(i) >= 0.0); b += a(i); i += 1 }
    assert(i > 0)
    i - 1
  }

  /** Draw a single sample from multinomial "a".  Assumes that the elements of "a" already sum to 1.0. */
  //@deprecated("Very inefficient due to boxing") def nextDiscrete (a:IndexedSeq[Double])(implicit r:Random): Int = {
  //  var b = 0.0; val s = nextUniform(r); var i = 0
  //  while (b <= s && i < a.length) { b += a(i); i += 1 }
  //  assert(i > 0)
  //  i - 1
  //}

  /** draw a single sample from (unnormalized) multinomial "a", with normalizing factor "sum". */
  def nextDiscrete (a:Array[Double], sum:Double)(implicit r:Random): Int = {
    assert(sum > 0.0, "sum = "+sum)
    var b = 0.0; val s = nextUniform(r) * sum; var i = 0
    while (b <= s && i < a.length) { assert(a(i) >= 0.0); b += a(i); i += 1 }
    assert(i > 0)
    i - 1
  }

  /*
   Directly adapted from:
   Michael Wichura,The Percentage Points of the Normal Distribution, 
   Applied Statistics, Volume 37, Number 3, pages 477-484, 1988.
   Algorithm AS 241,
   */
  
  // TODO Not thread-safe!
  private var nextGaussianValue = 0.0
  private var haveNextGaussianValue = false

  /** Return a random double drawn from a Gaussian distribution with mean 0 and variance 1. */
  def nextGaussian(implicit r:Random) : Double = {
    if (!haveNextGaussianValue) {
      val v1 = nextUniform(r); val v2 = nextUniform(r)
      val x1 = math.sqrt(-2*math.log(v1))*math.cos(2*math.Pi*v2);
      val x2 = math.sqrt(-2*math.log(v1))*math.sin(2*math.Pi*v2);
      nextGaussianValue = x2;
      haveNextGaussianValue = true;
      x1
    } else {
      haveNextGaussianValue = false;
      nextGaussian(r);
    }
  }

  /** Return a random double drawn from a Gaussian distribution with mean m and variance s2. */
  def nextGaussian(mean:Double, s2:Double)(implicit r:Random) :Double = nextGaussian(r) * math.sqrt(s2)+mean

  // generate Gamma(1,1)
  // E(X)=1 ; Var(X)=1
  /** Return a random double drawn from a Gamma distribution with mean 1.0 and variance 1.0. */
  def nextGamma(implicit r:Random) : Double = nextGamma(1,1,0)(r)

  /** Return a random double drawn from a Gamma distribution with mean alpha and variance 1.0. */
  def nextGamma(alpha:Double)(implicit r:Random) : Double = nextGamma(alpha,1,0)(r)

  /** Return a random double drawn from a Gamma distribution with mean alpha*beta and variance alpha*beta^2. */
  def nextGamma(alpha:Double, beta:Double)(implicit r:Random) : Double = nextGamma(alpha,beta,0)(r)

  /** Return a random double drawn from a Gamma distribution with mean alpha*beta+lamba and variance alpha*beta^2. */
  def nextGamma(alpha:Double, beta:Double, lambda:Double)(implicit r:Random) : Double = {
    var gamma = 0.0
    if (alpha <= 0.0 || beta <= 0.0) throw new IllegalArgumentException ("alpha and beta must be strictly positive.");
    if (alpha < 1.0) {
      var p = 0.0
      var flag = false;
      val b = 1+alpha*math.exp(-1)
      while (!flag) {
        p = b*nextUniform(r)
        if (p>1) {
          gamma = -math.log((b-p)/alpha);
          if (nextUniform(r) <= math.pow(gamma,alpha-1)) flag = true
        } else {
          gamma = math.pow(p,1/alpha);
          if (nextUniform(r) <= math.exp(-gamma)) flag = true
        }
      }
    } else if (alpha == 1) {
      gamma = -math.log (nextUniform(r))
    } else {
      var y = -math.log (nextUniform(r))
      while (nextUniform(r) > math.pow (y * math.exp (1 - y), alpha - 1))
        y = -math.log (nextUniform(r))
      gamma = alpha * y
    }
    beta * gamma + lambda;
  }

  /** Return a random double drawn from an Exponential distribution with mean 1 and variance 1. */
  def nextExp(implicit r:Random) = nextGamma(1,1,0)(r)

  /** Return a random double drawn from an Exponential distribution with mean beta and variance beta^2. */
  def nextExp(beta:Double)(implicit r:Random) = nextGamma(1,beta,0)(r)

  /** Return a random double drawn from an Exponential distribution with mean beta+lambda and variance beta^2. */
  def nextExp(beta:Double, lambda:Double)(implicit r:Random) = nextGamma(1,beta,lambda)(r)

  /** Return a random double drawn from an Chi-squarted distribution with mean 1 and variance 2. 
   * Equivalent to nextChiSq(1) */
  def nextChiSq(implicit r:Random) : Double = nextGamma(0.5,2,0)(r)

  /** Return a random double drawn from an Chi-squared distribution with mean df and variance 2*df.  */
  def nextChiSq(df:Int)(implicit r:Random) = nextGamma(0.5*df, 2, 0)(r)

  /** Return a random double drawn from an Chi-squared distribution with mean df+lambda and variance 2*df.  */
  def nextChiSq(df:Int, lambda:Double)(implicit r:Random) = nextGamma(0.5*df,2,lambda)(r)

  /** Return a random double drawn from a Beta distribution with mean a/(a+b) and variance ab/((a+b+1)(a+b)^2).  */
  def nextBeta(alpha:Double, beta:Double)(implicit r:Random) : Double = {
    if (alpha <= 0 || beta <= 0) throw new IllegalArgumentException ("alpha and beta must be strictly positive.")
    if (alpha == 1 && beta == 1) {
      return nextUniform(r)
    } else if (alpha >= 1 && beta >= 1) {
      val A = alpha - 1
      val B = beta - 1
      val C = A + B
      val L = C * math.log (C)
      val mu = A / C
      val sigma = 0.5 / math.sqrt (C)
      var y = nextGaussian(r)
      var x = sigma * y + mu
      while (x < 0 || x > 1) { y = nextGaussian(r); x = sigma * y + mu }
      var u = nextUniform(r)
      while (math.log (u) >= A * math.log (x / A) + B * math.log ((1 - x) / B) + L + 0.5 * y * y) {
        y = nextGaussian(r)
        x = sigma * y + mu
        while (x < 0 || x > 1) { y = nextGaussian(r); x = sigma * y + mu }
        u = nextUniform(r)
      }
      return x
    } else {
      var v1 = math.pow (nextUniform(r), 1 / alpha)
      var v2 = math.pow (nextUniform(r), 1 / beta)
      while (v1 + v2 > 1) {
        v1 = math.pow (nextUniform(r), 1 / alpha)
        v2 = math.pow (nextUniform(r), 1 / beta)
      }
      return v1 / (v1 + v2)
    }
  }



// Trig

  def cosh (a:Double) = if (a < 0) 0.5 * (math.exp(-a) + math.exp(a)) else 0.5 * (math.exp(a) + math.exp(-a))
  def tanh (a:Double) = (math.exp(a) - math.exp(-a)) / (math.exp(a) + math.exp(-a))




}
