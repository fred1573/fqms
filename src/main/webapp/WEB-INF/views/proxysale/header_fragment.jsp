<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/common/taglibs.jsp" %>
<div class="header">
    <div style="margin-left: 20px;">
        <security:authorize ifAnyGranted="ROLE_代销平台-渠道管理">
            <button urls="${ctx}/proxysale/ota" class="kc-btn <c:if test="${currentBtn == 'channel'}">kc-active</c:if>">
                渠道管理
            </button>
        </security:authorize>
        <security:authorize ifAnyGranted="ROLE_代销平台-客栈管理">
            <button urls="${ctx}/proxysale/inn" class="kc-btn <c:if test="${currentBtn == 'inn'}">kc-active</c:if>">
                客栈管理
            </button>
        </security:authorize>
        <security:authorize ifAnyGranted="ROLE_代销平台-代销审核">
            <button urls="${ctx}/proxysale/inn/price"
                    class="kc-btn <c:if test="${currentBtn == 'audit'}">kc-active</c:if>">代销审核
            </button>
        </security:authorize>
        <security:authorize ifAnyGranted="ROLE_代销平台-调价管理">
            <button urls="${ctx}/proxysale/price/list"
                    class="kc-btn <c:if test="${currentBtn == 'price'}">kc-active</c:if>">调价管理
            </button>
        </security:authorize>
        <security:authorize ifAnyGranted="ROLE_代销平台-已移除客栈">
            <button urls="${ctx}/proxysale/inn/del_list"
                    class="kc-btn <c:if test="${currentBtn == 'delList'}">kc-active</c:if>">已移除客栈
            </button>
        </security:authorize>
        <security:authorize ifAnyGranted="ROLE_代销操作记录">
            <button urls="${ctx}/proxysale/inn/operateList"
                    class="kc-btn <c:if test="${currentBtn == 'operate'}">kc-active</c:if>">操作记录
            </button>
        </security:authorize>
    </div>
</div>