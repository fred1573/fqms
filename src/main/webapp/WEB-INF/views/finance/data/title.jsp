<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<div style=" margin-left: 20px">
    <a href="${ctx}/data/statistics">
        <button urls="${ctx}/data/statistics"
                class="kc-btn <c:if test="${currentBtn == 'statistics'}">kc-active</c:if>">
            数据统计
        </button>
    </a>
    <a href="${ctx}/data/analysis">
        <button urls="${ctx}/data/analysis" class="kc-btn <c:if test="${currentBtn == 'analysis'}">kc-active</c:if>">
            数据分析
        </button>
    </a>
</div>
