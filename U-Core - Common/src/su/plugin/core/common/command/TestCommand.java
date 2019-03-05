package su.plugin.core.common.command;

import java.lang.reflect.Method;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.bytecode.ConstPool;
import javassist.bytecode.MethodInfo;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;
import su.plugin.core.common.api.Core;
import su.plugin.core.common.api.command.CommandHandler;
import su.plugin.core.common.api.command.UCommandListener;
import su.plugin.core.common.api.command.UCommandSender;
import su.plugin.core.common.api.util.DebugUtil;

public class TestCommand implements UCommandListener {

	@CommandHandler(
			name = "ctest",
			usePlatformPrefix = true,
			permission = "core.admin",
			usage = "테스트 명령어"
			)
	public void test(UCommandSender sender, String[] args) {
		DebugUtil.startTimeMeasurement(true);
		try {
			ClassPool cp = new ClassPool();
			cp.appendClassPath(Core.getUPluginManager().getUPluginByName("U-Core").getFile().getAbsolutePath());
			CtClass clazz = cp.getCtClass(DebugUtil.class.getName());

			for(CtMethod method : clazz.getMethods()) {
				Core.log("ㅡㅡㅡㅡㅡㅡㅡㅡㅡ");
				MethodInfo info = method.getMethodInfo2();
				ConstPool cpool = info.getConstPool();

				if(!method.getName().equalsIgnoreCase("exitTimeMeasurement")) continue;

				method.instrument(new ExprEditor() {
					public void edit(MethodCall m) throws CannotCompileException  {
						Core.log("// method: " + method.getName());
						Core.log("ClassName: " + m.getClassName());
						Core.log("MethodName: " + m.getMethodName());
						Core.log("signature: " + m.getSignature());

						if(m.getMethodName().equalsIgnoreCase("log")) {
							m.replace("System.out.println($1);");
						}
					}
				});

				for(Method rmethod : Core.class.getMethods()) {
					if(!rmethod.getName().equalsIgnoreCase("nbc")) continue;
					rmethod.invoke(clazz.toClass(), "ㅌㅅㅌ");
				}
				DebugUtil c = (DebugUtil) clazz.toClass().newInstance();
				c.exitTimeMeasurement(false);

				/*CodeAttribute cattr = info.getCodeAttribute();
				CodeIterator ci = cattr.iterator();
				while(ci.hasNext()) {
					int pos = ci.next();
					if(ci.byteAt(pos) == Opcode.INVOKESTATIC) {
						int index = ci.u16bitAt(pos + 1);
						String methodRefClassName = cpool.getMethodrefClassName(index);
						String methodRefMethodName = cpool.getMethodrefName(index);

						Core.log(method.getName() + ": methodRefClassName: " + methodRefClassName);
						Core.log(method.getName() + ": methodRefMethodName: " + methodRefMethodName);
						Core.log(method.getName() + ": methodRefType: " + cpool.getMethodrefType(index));
					}
				}*/
			}
			DebugUtil.exitTimeMeasurement(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}