// Generated from /home/fabris/Desktop/ufscar/cc/provling/src/main/antl4/br/ufscar/compiladores/provling/ProvlingLexer.g4 by ANTLR 4.9.2
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class ProvlingLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.9.2", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		PALAVRA_CHAVE=1, LETRA=2, IDENT=3, FRASE=4, INCOMPLETA_FRASE=5, COMENTARIO=6, 
		NUM_INT=7, NUM_REAL=8, CONFIG_ATRIB=9, IGNORAVEL=10, DESCONHECIDO=11;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"PALAVRA_CHAVE", "LETRA", "IDENT", "ESC_SEQ", "FRASE", "INCOMPLETA_FRASE", 
			"COMENTARIO", "NUM_INT", "NUM_REAL", "CONFIG_ATRIB", "IGNORAVEL", "DESCONHECIDO"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, null, null, null, null, null, null, null, null, "':'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "PALAVRA_CHAVE", "LETRA", "IDENT", "FRASE", "INCOMPLETA_FRASE", 
			"COMENTARIO", "NUM_INT", "NUM_REAL", "CONFIG_ATRIB", "IGNORAVEL", "DESCONHECIDO"
		};
	}
	private static final String[] _SYMBOLIC_NAMES = makeSymbolicNames();
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}


	public ProvlingLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "ProvlingLexer.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getChannelNames() { return channelNames; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2\r\u00a6\b\1\4\2\t"+
		"\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13"+
		"\t\13\4\f\t\f\4\r\t\r\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2"+
		"\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3"+
		"\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2"+
		"\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3"+
		"\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\5\2i\n\2\3\3\3\3\3"+
		"\4\6\4n\n\4\r\4\16\4o\3\5\3\5\3\5\3\6\3\6\3\6\7\6x\n\6\f\6\16\6{\13\6"+
		"\3\6\3\6\3\7\3\7\7\7\u0081\n\7\f\7\16\7\u0084\13\7\3\7\3\7\3\b\3\b\3\b"+
		"\3\b\7\b\u008c\n\b\f\b\16\b\u008f\13\b\3\b\3\b\3\t\6\t\u0094\n\t\r\t\16"+
		"\t\u0095\3\n\3\n\3\n\6\n\u009b\n\n\r\n\16\n\u009c\3\13\3\13\3\f\3\f\3"+
		"\f\3\f\3\r\3\r\2\2\16\3\3\5\4\7\5\t\2\13\6\r\7\17\b\21\t\23\n\25\13\27"+
		"\f\31\r\3\2\7\6\2\62;C\\aac|\4\2$$^^\3\2$$\4\2\f\f\17\17\4\2\13\f\17\17"+
		"\2\u00b4\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\13\3\2\2\2\2\r\3\2\2\2"+
		"\2\17\3\2\2\2\2\21\3\2\2\2\2\23\3\2\2\2\2\25\3\2\2\2\2\27\3\2\2\2\2\31"+
		"\3\2\2\2\3h\3\2\2\2\5j\3\2\2\2\7m\3\2\2\2\tq\3\2\2\2\13t\3\2\2\2\r~\3"+
		"\2\2\2\17\u0087\3\2\2\2\21\u0093\3\2\2\2\23\u0097\3\2\2\2\25\u009e\3\2"+
		"\2\2\27\u00a0\3\2\2\2\31\u00a4\3\2\2\2\33\34\7S\2\2\34\35\7W\2\2\35\36"+
		"\7G\2\2\36\37\7U\2\2\37 \7V\2\2 !\7\u00d7\2\2!\"\7G\2\2\"i\7U\2\2#$\7"+
		"S\2\2$%\7W\2\2%&\7G\2\2&\'\7U\2\2\'(\7V\2\2()\7\u00c5\2\2)i\7Q\2\2*+\7"+
		"R\2\2+,\7C\2\2,-\7T\2\2-i\7C\2\2./\7R\2\2/\60\7T\2\2\60\61\7Q\2\2\61\62"+
		"\7X\2\2\62i\7C\2\2\63\64\7G\2\2\64\65\7P\2\2\65\66\7W\2\2\66\67\7P\2\2"+
		"\678\7E\2\289\7K\2\29:\7C\2\2:;\7F\2\2;i\7Q\2\2<=\7C\2\2=>\7N\2\2>?\7"+
		"V\2\2?@\7G\2\2@A\7T\2\2AB\7P\2\2BC\7C\2\2CD\7V\2\2DE\7K\2\2EF\7X\2\2F"+
		"G\7C\2\2Gi\7U\2\2HI\7T\2\2IJ\7G\2\2JK\7U\2\2KL\7R\2\2LM\7Q\2\2MN\7U\2"+
		"\2NO\7V\2\2Oi\7C\2\2PQ\7G\2\2QR\7Z\2\2RS\7R\2\2ST\7N\2\2TU\7K\2\2UV\7"+
		"E\2\2VW\7C\2\2WX\7E\2\2XY\7Q\2\2YZ\7G\2\2Zi\7U\2\2[\\\7G\2\2\\]\7Z\2\2"+
		"]^\7R\2\2^_\7N\2\2_`\7K\2\2`a\7E\2\2ab\7C\2\2bc\7E\2\2cd\7C\2\2di\7Q\2"+
		"\2ef\7H\2\2fg\7K\2\2gi\7O\2\2h\33\3\2\2\2h#\3\2\2\2h*\3\2\2\2h.\3\2\2"+
		"\2h\63\3\2\2\2h<\3\2\2\2hH\3\2\2\2hP\3\2\2\2h[\3\2\2\2he\3\2\2\2i\4\3"+
		"\2\2\2jk\4C\\\2k\6\3\2\2\2ln\t\2\2\2ml\3\2\2\2no\3\2\2\2om\3\2\2\2op\3"+
		"\2\2\2p\b\3\2\2\2qr\7^\2\2rs\7$\2\2s\n\3\2\2\2ty\7$\2\2ux\5\t\5\2vx\n"+
		"\3\2\2wu\3\2\2\2wv\3\2\2\2x{\3\2\2\2yw\3\2\2\2yz\3\2\2\2z|\3\2\2\2{y\3"+
		"\2\2\2|}\7$\2\2}\f\3\2\2\2~\u0082\7$\2\2\177\u0081\n\4\2\2\u0080\177\3"+
		"\2\2\2\u0081\u0084\3\2\2\2\u0082\u0080\3\2\2\2\u0082\u0083\3\2\2\2\u0083"+
		"\u0085\3\2\2\2\u0084\u0082\3\2\2\2\u0085\u0086\t\5\2\2\u0086\16\3\2\2"+
		"\2\u0087\u0088\7\61\2\2\u0088\u0089\7\61\2\2\u0089\u008d\3\2\2\2\u008a"+
		"\u008c\n\5\2\2\u008b\u008a\3\2\2\2\u008c\u008f\3\2\2\2\u008d\u008b\3\2"+
		"\2\2\u008d\u008e\3\2\2\2\u008e\u0090\3\2\2\2\u008f\u008d\3\2\2\2\u0090"+
		"\u0091\b\b\2\2\u0091\20\3\2\2\2\u0092\u0094\4\62;\2\u0093\u0092\3\2\2"+
		"\2\u0094\u0095\3\2\2\2\u0095\u0093\3\2\2\2\u0095\u0096\3\2\2\2\u0096\22"+
		"\3\2\2\2\u0097\u0098\5\21\t\2\u0098\u009a\7\60\2\2\u0099\u009b\4\62;\2"+
		"\u009a\u0099\3\2\2\2\u009b\u009c\3\2\2\2\u009c\u009a\3\2\2\2\u009c\u009d"+
		"\3\2\2\2\u009d\24\3\2\2\2\u009e\u009f\7<\2\2\u009f\26\3\2\2\2\u00a0\u00a1"+
		"\t\6\2\2\u00a1\u00a2\3\2\2\2\u00a2\u00a3\b\f\2\2\u00a3\30\3\2\2\2\u00a4"+
		"\u00a5\13\2\2\2\u00a5\32\3\2\2\2\13\2howy\u0082\u008d\u0095\u009c\3\b"+
		"\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}